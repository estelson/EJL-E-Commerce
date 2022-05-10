package com.exemplo.ejle_commerce.autenticacao;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.databinding.ActivityRecuperarContaBinding;

public class RecuperarContaActivity extends AppCompatActivity {

    private ActivityRecuperarContaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecuperarContaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configClicks();
    }

    private void configClicks() {
        binding.include.ibVoltar.setOnClickListener(view -> {
            finish();
        });
    }
}