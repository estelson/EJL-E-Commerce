package com.exemplo.ejle_commerce.activity.loja;

import android.app.Activity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityLojaRecebimentosBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Loja;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LojaRecebimentosActivity extends AppCompatActivity {

    private ActivityLojaRecebimentosBinding binding;

    private Loja loja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaRecebimentosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configClicks();

        recuperarLoja();
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });

        binding.include.textTitulo.setText("Configs. de recebimentos");

        binding.btnSalvar.setOnClickListener(v -> {
            if(loja != null) {
                validarDados();
            } else {
                Toast.makeText(this, "Ainda estamos recuperando as informações da loja. Por favor aguarde.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void recuperarLoja() {
        DatabaseReference lojaRef = FirebaseHelper.getDatabaseReference()
                .child("loja");

        lojaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loja = snapshot.getValue(Loja.class);

                configDados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados() {
        if(loja.getPublicKey() != null) {
            binding.edtPublicKey.setText(loja.getPublicKey());
        }

        if(loja.getAccessToken() != null) {
            binding.edtAccessToken.setText(loja.getAccessToken());
        }

        if(loja.getParcelas() != 0) {
            binding.edtQtdeParcelas.setText(String.valueOf(loja.getParcelas()));
        }
    }

    private void validarDados() {
        String publicKey = binding.edtPublicKey.getText().toString().trim();
        String accessToken = binding.edtAccessToken.getText().toString().trim();

        String parcelasStr = binding.edtQtdeParcelas.getText().toString().trim();
        int parcelas = 0;
        if(!parcelasStr.isEmpty()) {
            parcelas = Integer.parseInt(binding.edtQtdeParcelas.getText().toString().trim());
        }

        if(!publicKey.isEmpty()) {
            if(!accessToken.isEmpty()) {
                if(parcelas > 0 && parcelas <= 12) {
                    ocultarTeclado();

                    loja.setPublicKey(publicKey);
                    loja.setAccessToken(accessToken);
                    loja.setParcelas(parcelas);

                    loja.salvar();
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

    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.edtPublicKey.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}