package com.exemplo.ejle_commerce.autenticacao;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityRecuperarContaBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;

public class RecuperarContaActivity extends AppCompatActivity {

    private ActivityRecuperarContaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecuperarContaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configClicks();
    }

    public void validarDados(View view) {
        String email = binding.edtEmail.getText().toString().trim();

        if(!email.isEmpty()) {
            binding.progressBar.setVisibility(View.VISIBLE);

            recuperarConta(email);
        } else {
            binding.edtEmail.requestFocus();
            binding.edtEmail.setError("Informe o e-Mail");
        }
    }

    private void recuperarConta(String email) {
        FirebaseHelper.getAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(this, "E-Mail de recuperação de senha enviado para " + email, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, FirebaseHelper.validaErros(task.getException().getMessage()), Toast.LENGTH_LONG).show();
                    }

                    binding.progressBar.setVisibility(View.GONE);
                });
    }

    private void configClicks() {
        binding.include.ibVoltar.setOnClickListener(view -> {
            finish();
        });
    }
}