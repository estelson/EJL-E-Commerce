package com.exemplo.ejle_commerce.activity.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.adapter.DetalhesPedidoAdapter;
import com.exemplo.ejle_commerce.databinding.ActivityDetalhesPedidoBinding;
import com.exemplo.ejle_commerce.model.Endereco;
import com.exemplo.ejle_commerce.model.Pedido;
import com.exemplo.ejle_commerce.util.GetMask;

public class DetalhesPedidoActivity extends AppCompatActivity {

    private ActivityDetalhesPedidoBinding binding;

    private DetalhesPedidoAdapter detalhesPedidoAdapter;

    private Pedido pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();

        getExtra();

        configClicks();
    }

    private void configRV() {
        binding.rvProdutos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProdutos.setHasFixedSize(true);

        detalhesPedidoAdapter = new DetalhesPedidoAdapter(pedido.getItensPedidoList(), this);

        binding.rvProdutos.setAdapter(detalhesPedidoAdapter);
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });
    }

    private void getExtra() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            pedido = (Pedido) bundle.getSerializable("pedidoSelecionado");

            configRV();

            configDados();
        }
    }

    private void configDados() {
        Endereco endereco = pedido.getEndereco();

        StringBuilder enderecoCompleto = new StringBuilder();
        enderecoCompleto
                .append(endereco.getLogradouro())
                .append(", ")
                .append(endereco.getNumero())
                .append("\n")
                .append(endereco.getBairro())
                .append(", ")
                .append(endereco.getLocalidade())
                .append(" - ")
                .append(endereco.getUf())
                .append("\nCEP: ")
                .append(endereco.getCep());

        binding.textEnderecoEntrega.setText(enderecoCompleto);

        binding.textNomePagamento.setText(pedido.getPagamento());

        double valorExtra;
        if(pedido.getAcrescimo() > 0) {
            binding.textTipoPagamento.setText("Acréscimo");
            valorExtra = pedido.getAcrescimo();
        } else {
            binding.textTipoPagamento.setText("Desconto");
            valorExtra = pedido.getDesconto() * -1;
        }

        binding.textValorTipoPagamento.setText(getString(R.string.valor, GetMask.getValor(valorExtra)));

        binding.textValorProdutos.setText(getString(R.string.valor, GetMask.getValor(pedido.getTotal())));

        binding.textValorTotal.setText(getString(R.string.valor, GetMask.getValor(pedido.getTotal() + valorExtra)));

    }

    private void iniciarComponentes() {
        binding.include.textTitulo.setText("Detalhes do pedido");
    }

}