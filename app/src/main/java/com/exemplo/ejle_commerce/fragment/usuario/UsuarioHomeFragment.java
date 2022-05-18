package com.exemplo.ejle_commerce.fragment.usuario;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.activity.usuario.DetalhesProdutoActivity;
import com.exemplo.ejle_commerce.adapter.CategoriaAdapter;
import com.exemplo.ejle_commerce.adapter.LojaProdutoAdapter;
import com.exemplo.ejle_commerce.databinding.FragmentUsuarioHomeBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Categoria;
import com.exemplo.ejle_commerce.model.Favorito;
import com.exemplo.ejle_commerce.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioHomeFragment extends Fragment implements CategoriaAdapter.OnClick, LojaProdutoAdapter.OnClickListener, LojaProdutoAdapter.OnClickFavorito {

    private FragmentUsuarioHomeBinding binding;

    private final List<Categoria> categoriasList = new ArrayList<>();

    private final List<Produto> produtosList = new ArrayList<>();

    private final List<String> idsFavoritos = new ArrayList<>();

    private CategoriaAdapter categoriaAdapter;

    private LojaProdutoAdapter lojaProdutoAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuarioHomeBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configRvCategorias();

        configRvProdutos();

        recuperarCategorias();

        recuperarFavoritos();
    }

    @Override
    public void onStart() {
        super.onStart();

        recuperarProdutos();
    }

    private void recuperarFavoritos() {
        if(FirebaseHelper.getAutenticado()) {
            DatabaseReference favoritoRef = FirebaseHelper.getDatabaseReference()
                    .child("favoritos")
                    .child(FirebaseHelper.getIdFirebase());

            favoritoRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    idsFavoritos.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        String idFavorito = ds.getValue(String.class);
                        idsFavoritos.add(idFavorito);
                    }

                    categoriaAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void configRvCategorias() {
        binding.rvCategorias.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategorias.setHasFixedSize(true);

        categoriaAdapter = new CategoriaAdapter(R.layout.item_categoria_horizontal, true, categoriasList, this);

        binding.rvCategorias.setAdapter(categoriaAdapter);
    }

    private void configRvProdutos() {
        binding.rvProdutos.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvProdutos.setHasFixedSize(true);

        lojaProdutoAdapter = new LojaProdutoAdapter(R.layout.item_produto_adapter, produtosList, requireContext(), true, idsFavoritos,  this, this);

        binding.rvProdutos.setAdapter(lojaProdutoAdapter);
    }

    private void recuperarProdutos() {
        DatabaseReference produtoRef = FirebaseHelper.getDatabaseReference()
                .child("produtos");

        produtoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtosList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Produto produto = ds.getValue(Produto.class);
                    produtosList.add(produto);
                }

                listEmpty();

                binding.progressBar.setVisibility(View.GONE);

                Collections.reverse(produtosList);

                lojaProdutoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listEmpty() {
        if(produtosList.isEmpty()) {
            binding.textInfo.setText("Nenhum produto cadastrado.");
        } else {
            binding.textInfo.setText("");
        }
    }

    private void recuperarCategorias() {
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference()
                .child("categorias");

        categoriaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriasList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Categoria categoria = ds.getValue(Categoria.class);
                    categoriasList.add(categoria);
                }

                Collections.reverse(categoriasList);

                categoriaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public void onClickListener(Categoria categoria) {
        Toast.makeText(requireContext(), categoria.getNome(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(Produto produto) {
        Intent intent = new Intent(requireContext(), DetalhesProdutoActivity.class);
        intent.putExtra("produtoSelecionado", produto);

        startActivity(intent);
    }

    @Override
    public void onClickFavorito(Produto produto) {
        if(!idsFavoritos.contains(produto.getId())) {
            idsFavoritos.add(produto.getId());
        } else {
            idsFavoritos.remove(produto.getId());
        }

        Favorito.salvar(idsFavoritos);
    }

}