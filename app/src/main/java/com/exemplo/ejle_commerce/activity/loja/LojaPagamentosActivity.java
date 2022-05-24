package com.exemplo.ejle_commerce.activity.loja;

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
    }

    private void iniciaComponentes() {
        binding.include.textTitulo.setText("Formas de pagamento");

        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });
    }

}