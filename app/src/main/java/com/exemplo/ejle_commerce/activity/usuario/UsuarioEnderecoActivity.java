package com.exemplo.ejle_commerce.activity.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.adapter.EnderecoAdapter;
import com.exemplo.ejle_commerce.databinding.ActivityUsuarioEnderecoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Endereco;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioEnderecoActivity extends AppCompatActivity implements EnderecoAdapter.OnCLickListener {

    private ActivityUsuarioEnderecoBinding binding;

    private EnderecoAdapter enderecoAdapter;

    private List<Endereco> enderecoList  = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioEnderecoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponentes();

        configClicks();

        configRv();
    }

    @Override
    protected void onStart() {
        super.onStart();

        recuperaEnderecos();
    }

    private void configRv() {
        binding.rvEndereco.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEndereco.setHasFixedSize(true);

        enderecoAdapter = new EnderecoAdapter(enderecoList, this, this);

        binding.rvEndereco.setAdapter(enderecoAdapter);
    }

    private void recuperaEnderecos() {
        DatabaseReference enderecoRef = FirebaseHelper.getDatabaseReference()
                .child("enderecos")
                .child(FirebaseHelper.getIdFirebase());

        enderecoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    enderecoList.clear();

                    for(DataSnapshot ds : snapshot.getChildren()) {
                        Endereco endereco = ds.getValue(Endereco.class);
                        enderecoList.add(endereco);
                    }

                    binding.textInfo.setText("");
                } else {
                    binding.textInfo.setText("Nenhum endereço cadastrado");
                }

                binding.progressBar.setVisibility(View.GONE);

                Collections.reverse(enderecoList);

                enderecoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void iniciaComponentes() {
        binding.include.textTitulo.setText("Meus endereços");
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });

        binding.include.btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, UsuarioFormEnderecoActivity.class));
        });
    }

    @Override
    public void onClick(Endereco endereco) {
        Toast.makeText(this, endereco.getNomeEndereco(), Toast.LENGTH_SHORT).show();
    }
}