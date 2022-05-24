package com.exemplo.ejle_commerce.activity.loja;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.adapter.LojaPagamentoAdapter;
import com.exemplo.ejle_commerce.databinding.ActivityLojaPagamentosBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.FormaPagamento;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LojaPagamentosActivity extends AppCompatActivity implements LojaPagamentoAdapter.OnClick {

    private ActivityLojaPagamentosBinding binding;

    private final List<FormaPagamento> formaPagamentoList = new ArrayList<>();

    private LojaPagamentoAdapter lojaPagamentoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaPagamentosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        iniciaComponentes();

        configClicks();

        configRv();

        recuperaFormasPagamento();
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

                lojaPagamentoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configRv() {
        binding.rvPagamentos.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPagamentos.setHasFixedSize(true);

        lojaPagamentoAdapter = new LojaPagamentoAdapter(formaPagamentoList, this, this);

        binding.rvPagamentos.setAdapter(lojaPagamentoAdapter);
    }

    private void configClicks() {
        binding.include.btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, LojaFormPagamentoActivity.class));
        });
    }

    private void iniciaComponentes() {
        binding.include.textTitulo.setText("Formas de pagamento");

        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public void onClickListener(FormaPagamento formaPagamento) {
        Intent intent = new Intent(this, LojaFormPagamentoActivity.class);
        intent.putExtra("formaPagamentoSelecionada", formaPagamento);

        startActivity(intent);
    }
}