package com.exemplo.ejle_commerce.activity.usuario;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityUsuarioSelecionaEnderecoBinding;

public class UsuarioSelecionaEnderecoActivity extends AppCompatActivity {

    private ActivityUsuarioSelecionaEnderecoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioSelecionaEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();

        configClicks();
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });
    }

    private void iniciarComponentes() {
        binding.include.textTitulo.setText("Endereço de entrega");
    }

}