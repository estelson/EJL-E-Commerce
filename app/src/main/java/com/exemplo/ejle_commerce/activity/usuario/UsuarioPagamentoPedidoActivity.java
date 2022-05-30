package com.exemplo.ejle_commerce.activity.usuario;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.dao.ItemDAO;
import com.exemplo.ejle_commerce.dao.ItemPedidoDAO;
import com.exemplo.ejle_commerce.databinding.ActivityUsuarioPagamentoPedidoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Endereco;
import com.exemplo.ejle_commerce.model.ItemPedido;
import com.exemplo.ejle_commerce.model.Loja;
import com.exemplo.ejle_commerce.model.Produto;
import com.exemplo.ejle_commerce.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class UsuarioPagamentoPedidoActivity extends AppCompatActivity {

    private ActivityUsuarioPagamentoPedidoBinding binding;

    private Endereco enderecoSelecionado;

    private Usuario usuario;

    private Loja loja;

    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    private List<ItemPedido> itemPedidoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioPagamentoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recuperarDados();

        binding.progressBar.setOnClickListener(v -> {
            configJSON();
        });
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

        Log.i("INFOTESTE", "configJSON: " + dados);
    }

    private void recuperarDados() {
        itemDAO = new ItemDAO(this);
        itemPedidoDAO = new ItemPedidoDAO(this);
        itemPedidoList = itemPedidoDAO.getList();

        recuperarUsuario();

        recuperarLoja();

        getExtra();
    }

    private void getExtra() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            enderecoSelecionado = (Endereco) bundle.getSerializable("enderecoSelecionado");
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}