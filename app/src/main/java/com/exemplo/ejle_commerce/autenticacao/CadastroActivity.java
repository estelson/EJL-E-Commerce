package com.exemplo.ejle_commerce.autenticacao;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityCadastroBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Loja;
import com.exemplo.ejle_commerce.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configClicks();
    }

    public void validarDados(View view) {
        String nome = binding.edtNome.getText().toString().trim();
        String email = binding.edtEmail.getText().toString().trim();
        String telefone = binding.edtTelefone.getMasked();
        String senha = binding.edtSenha.getText().toString().trim();
        String confirmarSenha = binding.edtConfirmarSenha.getText().toString().trim();

        if(!nome.isEmpty()) {
            if(!email.isEmpty()) {
                if(!telefone.isEmpty()) {
                    if(telefone.length() == 15) {

                        if (!senha.isEmpty()) {
                            if (!confirmarSenha.isEmpty()) {
                                if (senha.equals(confirmarSenha)) {
                                    binding.progressBar.setVisibility(View.VISIBLE);

                                    Usuario usuario = new Usuario();
                                    usuario.setNome(nome);
                                    usuario.setEmail(email);
                                    usuario.setTelefone(telefone);
                                    usuario.setSenha(senha);

                                    criarConta(usuario);
                                } else {
                                    binding.edtConfirmarSenha.requestFocus();
                                    binding.edtConfirmarSenha.setError("Senhas não conferem");
                                }
                            } else {
                                binding.edtConfirmarSenha.requestFocus();
                                binding.edtConfirmarSenha.setError("Confirme a senha");
                            }
                        } else {
                            binding.edtSenha.requestFocus();
                            binding.edtSenha.setError("Informe a senha");
                        }

                    } else {
                        binding.edtTelefone.requestFocus();
                        binding.edtTelefone.setError("Formato de telefone inválido");
                    }
                } else {
                    binding.edtTelefone.requestFocus();
                    binding.edtTelefone.setError("Informe o telefone do usuário");
                }
            } else {
                binding.edtEmail.requestFocus();
                binding.edtEmail.setError("Informe o e-mail");
            }
        } else {
            binding.edtNome.requestFocus();
            binding.edtNome.setError("Informe o nome");
        }
    }

    private void criarConta(Usuario usuario) {
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        String id = task.getResult().getUser().getUid();

                        usuario.setId(id);
                        usuario.salvar();

                        Intent intent = new Intent();
                        intent.putExtra("email", usuario.getEmail());
                        setResult(RESULT_OK, intent);
                        finish();

                        Toast.makeText(this, "Usuário incluído com sucesso", Toast.LENGTH_SHORT).show();
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

        binding.btnLogin.setOnClickListener(view -> {
            finish();
        });
    }

}