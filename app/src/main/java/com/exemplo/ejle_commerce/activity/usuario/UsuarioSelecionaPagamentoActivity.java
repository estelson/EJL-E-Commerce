package com.exemplo.ejle_commerce.activity.usuario;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.adapter.UsuarioPagamentoAdapter;
import com.exemplo.ejle_commerce.databinding.ActivityUsuarioSelecionaPagamentoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.FormaPagamento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioSelecionaPagamentoActivity extends AppCompatActivity implements UsuarioPagamentoAdapter.OnClick {

    private ActivityUsuarioSelecionaPagamentoBinding binding;

    private UsuarioPagamentoAdapter usuarioPagamentoAdapter;

    private final List<FormaPagamento> formaPagamentoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioSelecionaPagamentoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciarComponentes();

        configClicks();

        configRv();

        recuperaFormasPagamento();
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });
    }

    private void recuperaFormasPagamento() {
        DatabaseReference pagamentoRef = FirebaseHelper.getDatabaseReference()
                .child("formapagamento");

        pagamentoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    formaPagamentoList.clear();

                    for(DataSnapshot ds : snapshot.getChildren()) {
                        FormaPagamento formaPagamento = ds.getValue(FormaPagamento.class);
                        formaPagamentoList.add(formaPagamento);
                    }

                    binding.textInfo.setText("");
                } else {
                    binding.textInfo.setText("Nenhuma forma de pagamento cadastrada");
                }

                binding.progressBar.setVisibility(View.GONE);

                Collections.reverse(formaPagamentoList);

                usuarioPagamentoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configRv() {
        binding.rvPagamentos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPagamentos.setHasFixedSize(true);

        usuarioPagamentoAdapter = new UsuarioPagamentoAdapter(formaPagamentoList, this);

        binding.rvPagamentos.setAdapter(usuarioPagamentoAdapter);
    }

    private void iniciarComponentes() {
        binding.include.textTitulo.setText("Formas de pagamento");
    }

    @Override
    public void onClickListener(FormaPagamento formaPagamento) {
        Toast.makeText(this, formaPagamento.getNome(), Toast.LENGTH_SHORT).show();
    }
}