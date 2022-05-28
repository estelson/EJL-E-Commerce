package com.exemplo.ejle_commerce.activity.usuario;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.api.CEPService;
import com.exemplo.ejle_commerce.databinding.ActivityUsuarioFormEnderecoBinding;
import com.exemplo.ejle_commerce.model.Endereco;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsuarioFormEnderecoActivity extends AppCompatActivity {

    private ActivityUsuarioFormEnderecoBinding binding;

    private Endereco endereco;

    private boolean novoEndereco = true;

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioFormEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponentes();

        configClicks();

        getExtra();

        iniciarRetrofit();
    }

    private void getExtra() {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            endereco = (Endereco) bundle.getSerializable("enderecoSelecionado");

            configDados();

            novoEndereco = false;
        }
    }

    private void iniciarRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://viacep.com.br/ws/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private void configDados() {
        binding.edtNomeEndereco.setText(endereco.getNomeEndereco());
        binding.edtCEP.setText(endereco.getCep());
        binding.edtUF.setText(endereco.getUf());
        binding.edtNumEndereco.setText(endereco.getNumero());
        binding.edtLogradouro.setText(endereco.getLogradouro());
        binding.edtBairro.setText(endereco.getBairro());
        binding.edtMunicipio.setText(endereco.getLocalidade());
    }

    private void iniciaComponentes() {
        if(novoEndereco) {
            binding.include.textTitulo.setText("Adicionar endereço");
        } else {
            binding.include.textTitulo.setText("Alterar endereço");
        }
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });

        binding.include.btnSalvar.setOnClickListener(v -> {
            validarDados();
        });

        binding.btnBuscar.setOnClickListener(v -> {
            buscarCep();
        });
    }

    private void buscarCep() {
        String cep = binding.edtCEP.getMasked()
                .replace("-", "")
                .replaceAll("_", "");

        if(cep.length() == 8) {
            ocultarTeclado();

            binding.progressBar.setVisibility(View.VISIBLE);

            CEPService cepService = retrofit.create(CEPService.class);
            Call<Endereco> call = cepService.recuperarCEP(cep);

            call.enqueue(new Callback<Endereco>() {
                @Override
                public void onResponse(@NonNull Call<Endereco> call, @NonNull Response<Endereco> response) {
                    if(response.isSuccessful()) {
                        String idEndereco = "";
                        String nomeEndereco = "";
                        String numEndereco = "";
                        if(!novoEndereco) {
                            idEndereco = endereco.getId();
                            nomeEndereco = endereco.getNomeEndereco();
                            numEndereco = endereco.getNumero();
                        }

                        endereco = response.body();

                        if(endereco != null) {
                            if (endereco.getLocalidade() != null) {
                                if(!novoEndereco) {
                                    endereco.setId(idEndereco);
                                }
                                endereco.setNomeEndereco(nomeEndereco);
                                endereco.setNumero(numEndereco);

                                configDados();
                            } else {
                                Toast.makeText(getBaseContext(), "Não foi possível recuperar o endereço", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getBaseContext(), "Não foi possível recuperar o endereço", Toast.LENGTH_SHORT).show();
                        }

                        binding.progressBar.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Endereco> call, Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);

                    Toast.makeText(getBaseContext(), "Erro na comunicação com o serviço de busca de CEP", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Formato de CEP inválido", Toast.LENGTH_SHORT).show();
        }
    }

    private void validarDados() {
        String nomeEndereco = binding.edtNomeEndereco.getText().toString().trim();
        String cep = binding.edtCEP.getMasked();
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

                                binding.progressBar.setVisibility(View.GONE);

                                if(novoEndereco) {
                                    Intent intent = new Intent();
                                    intent.putExtra("enderecoCadastrado", endereco);

                                    setResult(RESULT_OK, intent);

                                    finish();

                                    Toast.makeText(this, "Endereço incluído com sucesso", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(this, "Endereço alterado com sucesso", Toast.LENGTH_SHORT).show();
                                }
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