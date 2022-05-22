package com.exemplo.ejle_commerce.activity.usuario;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.databinding.ActivityUsuarioFormEnderecoBinding;
import com.exemplo.ejle_commerce.model.Endereco;

public class UsuarioFormEnderecoActivity extends AppCompatActivity {

    private ActivityUsuarioFormEnderecoBinding binding;

    private Endereco endereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioFormEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponentes();

        configClicks();
    }

    private void iniciaComponentes() {
        binding.include.textTitulo.setText("Adicionar endereço");
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });

        binding.include.btnSalvar.setOnClickListener(v -> {
            validarDados();
        });
    }

    private void validarDados() {
        String nomeEndereco = binding.edtNomeEndereco.getText().toString().trim();
        String cep = binding.edtCEP.getText().toString().trim();
        String uf = binding.edtUF.getText().toString().trim();
        String numero = binding.edtNumEndereco.getText().toString().trim();
        String logradouro = binding.edtLogradouro.getText().toString().trim();
        String bairro = binding.edtBairro.getText().toString().trim();
        String municipio = binding.edtMunicipio.getText().toString().trim();

        if(!nomeEndereco.isEmpty()) {
            if(!cep.isEmpty()) {
                if(!uf.isEmpty()) {
                    if(!logradouro.isEmpty()) {
                        if(!bairro.isEmpty()) {
                            if(!municipio.isEmpty()) {
                                ocultarTeclado();

                                binding.progressBar.setVisibility(View.VISIBLE);

                                if(endereco == null) {
                                    endereco = new Endereco();
                                }

                                endereco.setNomeEndereco(nomeEndereco);
                                endereco.setCep(cep);
                                endereco.setUf(uf);
                                endereco.setNumero(numero);
                                endereco.setLogradouro(logradouro);
                                endereco.setBairro(bairro);
                                endereco.setLocalidade(municipio);

                                endereco.salvar();

                                finish();
                            } else {
                                binding.edtMunicipio.requestFocus();
                                binding.edtMunicipio.setError("Informe o município");
                            }
                        } else {
                            binding.edtBairro.requestFocus();
                            binding.edtBairro.setError("Informe o bairro");
                        }
                    } else {
                        binding.edtLogradouro.requestFocus();
                        binding.edtLogradouro.setError("Informe o logradouro");
                    }
                } else {
                    binding.edtUF.requestFocus();
                    binding.edtUF.setError("Informe a UF");
                }
            } else {
                binding.edtCEP.requestFocus();
                binding.edtCEP.setError("Informe o CEP");
            }
        } else {
            binding.edtNomeEndereco.requestFocus();
            binding.edtNomeEndereco.setError("Informe o nome do endereço");
        }
    }

    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(binding.edtNomeEndereco.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

}