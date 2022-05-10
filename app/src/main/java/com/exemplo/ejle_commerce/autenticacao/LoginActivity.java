package com.exemplo.ejle_commerce.autenticacao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityLoginBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    String email = result.getData().getStringExtra("email");
                    binding.edtEmail.setText(email);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configClicks();
    }

    public void validarDados(View view) {
        String email = binding.edtEmail.getText().toString().trim();
        String senha = binding.edtSenha.getText().toString().trim();

        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {
                binding.progressBar.setVisibility(View.VISIBLE);

                login(email, senha);
            } else {
                binding.edtSenha.requestFocus();
                binding.edtSenha.setError("Informe a senha");
            }
        } else {
            binding.edtEmail.requestFocus();
            binding.edtEmail.setError("Informe o e-mail");
        }
    }

    private void login(String email, String senha) {
        FirebaseHelper.getAuth().signInWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                finish();
            } else {
                binding.progressBar.setVisibility(View.GONE);

                Toast.makeText(this, FirebaseHelper.validaErros(task.getException().getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configClicks() {
        binding.include.ibVoltar.setOnClickListener(view -> {
            finish();
        });

        binding.btnRecuperarSenha.setOnClickListener(view -> {
            startActivity(new Intent(this, RecuperarContaActivity.class));
        });

        binding.btnCadastro.setOnClickListener(view -> {
            Intent intent = new Intent(this, CadastroActivity.class);
            resultLauncher.launch(intent);
        });
    }

}