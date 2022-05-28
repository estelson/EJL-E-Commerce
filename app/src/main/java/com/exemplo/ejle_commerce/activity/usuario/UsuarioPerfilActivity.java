package com.exemplo.ejle_commerce.activity.usuario;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityUsuarioPerfilBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UsuarioPerfilActivity extends AppCompatActivity {

    private ActivityUsuarioPerfilBinding binding;

    Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();

        configClicks();

        recuperarUsuario();
    }

    private void validarDados() {
        String nome = binding.edtNome.getText().toString().trim();
        String telefone = binding.edtTelefone.getMasked();

        if(!nome.isEmpty()) {
            if(!telefone.isEmpty()) {
                if (telefone.length() == 15) {
                    ocultarTeclado();

                    binding.progressBar.setVisibility(View.VISIBLE);

                    if(usuario != null) {
                        usuario.setNome(nome);
                        usuario.setTelefone(telefone);

                        usuario.salvar();

                        Toast.makeText(this, "Dados do usuário alterados com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Aguarde, as informações ainda estão sendo recuperadas", Toast.LENGTH_LONG).show();
                    }

                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    binding.edtTelefone.requestFocus();
                    binding.edtTelefone.setError("Formato de telefone inválido");
                }
            } else {
                binding.edtTelefone.requestFocus();
                binding.edtTelefone.setError("Informe o telefone do usuário");
            }
        } else {
            binding.edtNome.requestFocus();
            binding.edtNome.setError("Informe o nome do usuário");
        }
    }

    private void configDados() {
        binding.edtNome.setText(usuario.getNome());
        binding.edtTelefone.setText(usuario.getTelefone());
        binding.edtEmail.setText(usuario.getEmail());

        binding.progressBar.setVisibility(View.GONE);
    }

    private void recuperarUsuario() {
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());

        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);

                configDados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });

        binding.include.btnSalvar.setOnClickListener(v -> {
            validarDados();
        });
    }

    private void iniciarComponentes() {
        binding.include.textTitulo.setText("Meus dados");
    }

    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.edtNome.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}