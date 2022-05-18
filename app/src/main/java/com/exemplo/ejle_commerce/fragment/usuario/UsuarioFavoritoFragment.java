package com.exemplo.ejle_commerce.fragment.usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.adapter.LojaProdutoAdapter;
import com.exemplo.ejle_commerce.databinding.FragmentUsuarioFavoritoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Favorito;
import com.exemplo.ejle_commerce.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioFavoritoFragment extends Fragment implements LojaProdutoAdapter.OnClickListener, LojaProdutoAdapter.OnClickFavorito {

    private FragmentUsuarioFavoritoBinding binding;

    private final List<Produto> produtosList = new ArrayList<>();

    private final List<String> idsFavoritos = new ArrayList<>();

    private LojaProdutoAdapter lojaProdutoAdapter;

    private DatabaseReference favoritoRef;
    private ValueEventListener eventListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuarioFavoritoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configRvProdutos();

        recuperarFavoritos();
    }

    private void configRvProdutos() {
        binding.rvProdutos.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvProdutos.setHasFixedSize(true);

        lojaProdutoAdapter = new LojaProdutoAdapter(R.layout.item_produto_adapter, produtosList, requireContext(), true, idsFavoritos,  this, this);

        binding.rvProdutos.setAdapter(lojaProdutoAdapter);
    }

    private void recuperarFavoritos() {
        if(FirebaseHelper.getAutenticado()) {
            favoritoRef = FirebaseHelper.getDatabaseReference()
                    .child("favoritos")
                    .child(FirebaseHelper.getIdFirebase());

            eventListener = favoritoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    idsFavoritos.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String idFavorito = ds.getValue(String.class);
                        idsFavoritos.add(idFavorito);
                    }

                    Collections.reverse(idsFavoritos);

                    listEmpty();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void recuperarProdutos() {
        produtosList.clear();

        for (int i = 0; i < idsFavoritos.size(); i++) {
            DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                    .child("produtos")
                    .child(idsFavoritos.get(i));

            produtoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Produto produto = snapshot.getValue(Produto.class);
                    produtosList.add(produto);

                    if(produtosList.size() == idsFavoritos.size()) {
                        binding.progressBar.setVisibility(View.GONE);

                        lojaProdutoAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void listEmpty() {
        if(idsFavoritos.isEmpty()) {
            binding.textInfo.setText("Nenhum produto adicionado à sua lista de desejos.");

            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.textInfo.setText("");

            recuperarProdutos();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        favoritoRef.removeEventListener(eventListener);

        binding = null;
    }

    @Override
    public void onClick(Produto produto) {

    }

    @Override
    public void onClickFavorito(Produto produto) {
        if(!idsFavoritos.contains(produto.getId())) {
            idsFavoritos.add(produto.getId());
            produtosList.add(produto);
        } else {
            idsFavoritos.remove(produto.getId());
            produtosList.remove(produto);
        }

        Favorito.salvar(idsFavoritos);

        lojaProdutoAdapter.notifyDataSetChanged();
    }
}