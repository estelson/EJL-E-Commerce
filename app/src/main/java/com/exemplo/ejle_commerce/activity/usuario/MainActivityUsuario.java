package com.exemplo.ejle_commerce.activity.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.autenticacao.LoginActivity;
import com.exemplo.ejle_commerce.databinding.ActivityMainUsuarioBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;

public class MainActivityUsuario extends AppCompatActivity {

    private ActivityMainUsuarioBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainUsuarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(view -> {
            if(FirebaseHelper.getAutenticado()) {
                FirebaseHelper.getAuth().signOut();

                Toast.makeText(this, "Usuário já autenticado", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }
}