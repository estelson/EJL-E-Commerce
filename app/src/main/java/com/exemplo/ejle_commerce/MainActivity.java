package com.exemplo.ejle_commerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.autenticacao.LoginActivity;
import com.exemplo.ejle_commerce.databinding.ActivityMainBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(view -> {
            if(FirebaseHelper.getAutenticado()) {
                Toast.makeText(this, "Usuário já autenticado", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }
}