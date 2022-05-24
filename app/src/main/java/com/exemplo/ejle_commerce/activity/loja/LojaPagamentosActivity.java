package com.exemplo.ejle_commerce.activity.loja;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityLojaPagamentosBinding;

public class LojaPagamentosActivity extends AppCompatActivity {

    private ActivityLojaPagamentosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaPagamentosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponentes();

        configClicks();
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

}