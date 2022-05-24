package com.exemplo.ejle_commerce.activity.loja;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityLojaFormPagamentoBinding;

public class LojaFormPagamentoActivity extends AppCompatActivity {

    private ActivityLojaFormPagamentoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaFormPagamentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }



}