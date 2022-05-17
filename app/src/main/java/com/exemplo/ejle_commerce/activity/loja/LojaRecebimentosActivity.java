package com.exemplo.ejle_commerce.activity.loja;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityLojaRecebimentosBinding;

public class LojaRecebimentosActivity extends AppCompatActivity {

    private ActivityLojaRecebimentosBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaRecebimentosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configClicks();
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });

        binding.include.textTitulo.setText("Configs. de recebimentos");

        validarDados();
    }

    private void validarDados() {
        String publicKey = binding.edtPublicKey.getText().toString().trim();
        String accessToken = binding.edtAccessToken.getText().toString().trim();
        int parcelas = Integer.parseInt(binding.edtQtdeParcelas.getText().toString().trim());

        if(!publicKey.isEmpty()) {
            if(!accessToken.isEmpty()) {
                if(parcelas > 0 && parcelas <= 12) {

                } else {
                    binding.edtQtdeParcelas.requestFocus();
                    binding.edtQtdeParcelas.setError("Informe uma quantidade de parcelas entre 1 e 12");
                }
            } else {
                binding.edtAccessToken.requestFocus();
                binding.edtAccessToken.setError("Informe o Access Token");
            }
        } else {
            binding.edtPublicKey.requestFocus();
            binding.edtPublicKey.setError("Informe a Public Key");
        }
    }

}