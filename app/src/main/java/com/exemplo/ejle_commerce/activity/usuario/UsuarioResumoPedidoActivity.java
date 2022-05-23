package com.exemplo.ejle_commerce.activity.usuario;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityUsuarioResumoPedidoBinding;

public class UsuarioResumoPedidoActivity extends AppCompatActivity {

    private ActivityUsuarioResumoPedidoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioResumoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}