package com.exemplo.ejle_commerce.activity.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.api.MercadoPagoService;
import com.exemplo.ejle_commerce.dao.ItemDAO;
import com.exemplo.ejle_commerce.dao.ItemPedidoDAO;
import com.exemplo.ejle_commerce.databinding.ActivityUsuarioPagamentoPedidoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Endereco;
import com.exemplo.ejle_commerce.model.FormaPagamento;
import com.exemplo.ejle_commerce.model.ItemPedido;
import com.exemplo.ejle_commerce.model.Loja;
import com.exemplo.ejle_commerce.model.Pedido;
import com.exemplo.ejle_commerce.model.Produto;
import com.exemplo.ejle_commerce.model.StatusPedido;
import com.exemplo.ejle_commerce.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.model.Payment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsuarioPagamentoPedidoActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 100;

    private ActivityUsuarioPagamentoPedidoBinding binding;

    private FormaPagamento formaPagamento;

    private Endereco enderecoSelecionado;

    private Usuario usuario;

    private Loja loja;

    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    private List<ItemPedido> itemPedidoList = new ArrayList<>();

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioPagamentoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recuperarDados();

//        binding.progressBar.setOnClickListener(v -> {
//            configJSON();
//        });

        iniciarRetrofit();
    }

    private void configJSON() {
        JsonObject dados = new JsonObject();

        JsonArray itemsList = new JsonArray();

        JsonObject payer = new JsonObject();
        JsonObject phone = new JsonObject();
        JsonObject address = new JsonObject();
        JsonObject payment_methods = new JsonObject();

        String telefone = usuario.getTelefone()
                .replace("(", "")
                .replace(")", "")
                .replace(" ", "");

        phone.addProperty("area_code", telefone.substring(0, 2));
        phone.addProperty("number", telefone.substring(2, 12));

        address.addProperty("street_name", enderecoSelecionado.getLogradouro());
        if(enderecoSelecionado.getNumero() != null) {
            address.addProperty("street_number", enderecoSelecionado.getNumero());
        }
        address.addProperty("zip_code", enderecoSelecionado.getCep());

        payment_methods.addProperty("installments", loja.getParcelas());

        JsonObject item;
        for (ItemPedido itemPedido : itemPedidoList) {
            Produto produto = itemPedidoDAO.getProduto(itemPedido.getId());

            item = new JsonObject();
            item.addProperty("title", produto.getTitulo());
            item.addProperty("currency_id", "BRL");
            item.addProperty("picture_url", produto.getUrlsImagens().get(0).getCaminhoImagem());
            item.addProperty("quantity", itemPedido.getQuantidade());
            item.addProperty("unit_price", produto.getValorAtual());

            itemsList.add(item);

            itemPedido.setNomeProduto(produto.getTitulo());
        }

        dados.add("items", itemsList);

        payer.addProperty("name", usuario.getNome());
        payer.addProperty("email", usuario.getEmail());
        payer.add("phone", phone);
        payer.add("address", address);

        dados.add("payer", payer);
        dados.add("payment_methods", payment_methods);

        efetuarPagamento(dados);

//        Log.i("INFOTESTE", "configJSON: " + dados);
    }

    private void iniciarRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.mercadopago.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void efetuarPagamento(JsonObject dados) {
        String url = "checkout/preferences?access_token=" + loja.getAccessToken();

        MercadoPagoService mercadoPagoService = retrofit.create(MercadoPagoService.class);

        Call<JsonObject> call = mercadoPagoService.efetuarPagamento(url, dados);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                String id = response.body().get("id").getAsString();

                continuaPagamento(id);
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

            }
        });
    }

    private void continuaPagamento(String idPagamento) {
        final AdvancedConfiguration advancedConfiguration = new AdvancedConfiguration.Builder().setBankDealsEnabled(false).build();

        new MercadoPagoCheckout
                .Builder(loja.getPublicKey(), idPagamento)
                .setAdvancedConfiguration(advancedConfiguration)
                .build()
                .startPayment(this, REQUEST_CODE);
    }

    private void recuperarDados() {
        itemDAO = new ItemDAO(this);
        itemPedidoDAO = new ItemPedidoDAO(this);
        itemPedidoList = itemPedidoDAO.getList();

        recuperarUsuario();

        getExtra();
    }

    private void getExtra() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            enderecoSelecionado = (Endereco) bundle.getSerializable("enderecoSelecionado");
            formaPagamento = (FormaPagamento) bundle.getSerializable("pagamentoSelecionado");
        }
    }

    private void recuperarUsuario() {
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);

                if(usuario != null) {
                    recuperarLoja();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarLoja() {
        DatabaseReference lojaRef = FirebaseHelper.getDatabaseReference()
                .child("loja");

        lojaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loja = snapshot.getValue(Loja.class);

                if(loja != null) {
                    configJSON();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void validarRetorno(Payment payment) {
        // approved, rejected, in_process

        String status = payment.getPaymentStatus();
        switch (status) {
            case "approved":
                finalizarPedido(StatusPedido.APROVADO);
                break;
            case "rejected":
                finalizarPedido(StatusPedido.CANCELADO);
                break;
            case "in_process":
                finalizarPedido(StatusPedido.PENDENTE);
                break;
        }
    }

    private void finalizarPedido(StatusPedido statusPedido) {
        Pedido pedido = new Pedido();
        pedido.setIdCliente(FirebaseHelper.getIdFirebase());
        pedido.setEndereco(enderecoSelecionado);
        pedido.setTotal(itemPedidoDAO.getTotalPedido());
        pedido.setPagamento(formaPagamento.getNome());
        pedido.setStatusPedido(statusPedido);

        if(formaPagamento.getTipoValor().equals("DESC")) {
            pedido.setDesconto(formaPagamento.getValor());
        } else {
            pedido.setAcrescimo(formaPagamento.getValor());
        }

        pedido.setItensPedidoList(itemPedidoDAO.getList());

        pedido.salvar(true);

        // limpar o carrino de compras
        itemPedidoDAO.limparCarrinho();

        Intent intent = new Intent(this, MainActivityUsuario.class);
        // limpa a pilha de activities abertas
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("id", 1);

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            Payment payment = (Payment) data.getSerializableExtra(MercadoPagoCheckout.EXTRA_PAYMENT_RESULT);

            validarRetorno(payment);
        } else {
            Toast.makeText(this, "Erro ao efetuar o pagamento", Toast.LENGTH_SHORT).show();
        }
    }

}