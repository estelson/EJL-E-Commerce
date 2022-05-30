package com.exemplo.ejle_commerce.activity.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.databinding.ActivityUsuarioPagamentoPedidoBinding;

public class UsuarioPagamentoPedidoActivity extends AppCompatActivity {

    private ActivityUsuarioPagamentoPedidoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioPagamentoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}