package com.exemplo.ejle_commerce.activity.usuario;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.adapter.EnderecoSelecaoAdapter;
import com.exemplo.ejle_commerce.databinding.ActivityUsuarioSelecionaEnderecoBinding;
import com.exemplo.ejle_commerce.model.Endereco;

import java.util.ArrayList;
import java.util.List;

public class UsuarioSelecionaEnderecoActivity extends AppCompatActivity implements EnderecoSelecaoAdapter.OnCLickListener {

    private ActivityUsuarioSelecionaEnderecoBinding binding;

    private EnderecoSelecaoAdapter enderecoSelecaoAdapter;

    private final List<Endereco> enderecoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioSelecionaEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();

        configClicks();

        configRv();
    }

    private void configRv() {
        binding.rvEnderecos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEnderecos.setHasFixedSize(true);

        enderecoSelecaoAdapter = new EnderecoSelecaoAdapter(enderecoList, this);

        binding.rvEnderecos.setAdapter(enderecoSelecaoAdapter);
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });
    }

    private void iniciarComponentes() {
        binding.include.textTitulo.setText("Endereço de entrega");
    }

    @Override
    public void onClick(Endereco endereco) {
        Toast.makeText(this, endereco.getNomeEndereco(), Toast.LENGTH_SHORT).show();
    }

}