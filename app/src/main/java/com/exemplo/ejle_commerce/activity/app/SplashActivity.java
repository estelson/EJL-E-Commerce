package com.exemplo.ejle_commerce.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.activity.usuario.MainActivityUsuario;
import com.exemplo.ejle_commerce.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(getMainLooper()).postDelayed(() -> {
            finish();
            startActivity(new Intent(this, MainActivityUsuario.class));
        }, 3000);
    }

}