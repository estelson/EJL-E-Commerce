package com.exemplo.ejle_commerce.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.activity.loja.MainActivityEmpresa;
import com.exemplo.ejle_commerce.activity.usuario.MainActivityUsuario;
import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        new Handler(getMainLooper()).postDelayed(() -> {
//            verificarAcesso();
//        }, 3000);
        new Handler(getMainLooper()).postDelayed(this::verificarAcesso, 3000);
    }

    private void verificarAcesso() {
        if(FirebaseHelper.getAutenticado()) {
            recuperarAcesso();
        } else {
            finish();
            startActivity(new Intent(this, MainActivityUsuario.class));
        }
    }

    private void recuperarAcesso() {
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                finish();

                if(snapshot.exists()) { // Se for usuário...
                    startActivity(new Intent(getBaseContext(), MainActivityUsuario.class));
                } else { // Se for a loja
                    startActivity(new Intent(getBaseContext(), MainActivityEmpresa.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}