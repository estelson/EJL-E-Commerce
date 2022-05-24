package com.exemplo.ejle_commerce.activity.loja;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.adapter.LojaPagamentoAdapter;
import com.exemplo.ejle_commerce.databinding.ActivityLojaPagamentosBinding;
import com.exemplo.ejle_commerce.model.FormaPagamento;

import java.util.ArrayList;
import java.util.List;

public class LojaPagamentosActivity extends AppCompatActivity implements LojaPagamentoAdapter.OnClick {

    private ActivityLojaPagamentosBinding binding;

    private List<FormaPagamento> formaPagamentoList = new ArrayList<>();

    private LojaPagamentoAdapter lojaPagamentoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaPagamentosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponentes();

        configClicks();

        configRv();
    }

    private void configRv() {
        binding.rvPagamentos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPagamentos.setHasFixedSize(true);

        lojaPagamentoAdapter = new LojaPagamentoAdapter(formaPagamentoList, this, this);

        binding.rvPagamentos.setAdapter(lojaPagamentoAdapter);
    }

    private void configClicks() {
        binding.include.btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, LojaFormPagamentoActivity.class));
        });
    }

    private void iniciaComponentes() {
        binding.include.textTitulo.setText("Formas de pagamento");

        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onClickListener(FormaPagamento formaPagamento) {

    }
}