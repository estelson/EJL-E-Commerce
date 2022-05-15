package com.exemplo.ejle_commerce.fragment.loja;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.exemplo.ejle_commerce.activity.loja.LojaFormProdutoActivity;
import com.exemplo.ejle_commerce.adapter.LojaProdutoAdapter;
import com.exemplo.ejle_commerce.databinding.FragmentLojaProdutoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LojaProdutoFragment extends Fragment implements LojaProdutoAdapter.OnClickListener {

    private List<Produto> produtosList = new ArrayList<>();

    private LojaProdutoAdapter lojaProdutoAdapter;

    private FragmentLojaProdutoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        binding = FragmentLojaProdutoBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configClicks();

        configRv();
    }

    @Override
    public void onStart() {
        super.onStart();

        recuperarProdutos();
    }

    private void configClicks() {
        binding.toolbar.btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), LojaFormProdutoActivity.class));
        });
    }

    private void configRv() {
        binding.rvProdutos.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvProdutos.setHasFixedSize(true);

        lojaProdutoAdapter = new LojaProdutoAdapter(produtosList, requireContext(), this);

        binding.rvProdutos.setAdapter(lojaProdutoAdapter);
    }

    private void recuperarProdutos() {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos");

        produtoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    produtosList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Produto produto = ds.getValue(Produto.class);
                        produtosList.add(produto);
                    }

                    binding.textInfo.setText("");
                } else {
                    binding.textInfo.setText("Nenhum produto cadastrado.");
                }

                binding.progressBar.setVisibility(View.GONE);

                Collections.reverse(produtosList);

                lojaProdutoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialog(Produto produto) {

    }

    @Override
    public void onClick(Produto produto) {
        showDialog(produto);
    }

}