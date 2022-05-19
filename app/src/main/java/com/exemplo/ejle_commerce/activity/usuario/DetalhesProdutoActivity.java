package com.exemplo.ejle_commerce.activity.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.adapter.LojaProdutoAdapter;
import com.exemplo.ejle_commerce.adapter.SliderAdapter;
import com.exemplo.ejle_commerce.dao.ItemDAO;
import com.exemplo.ejle_commerce.dao.ItemPedidoDAO;
import com.exemplo.ejle_commerce.databinding.ActivityDetalhesProdutoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Favorito;
import com.exemplo.ejle_commerce.model.ItemPedido;
import com.exemplo.ejle_commerce.model.Produto;
import com.exemplo.ejle_commerce.util.GetMask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetalhesProdutoActivity extends AppCompatActivity implements LojaProdutoAdapter.OnClickListener, LojaProdutoAdapter.OnClickFavorito {

    private ActivityDetalhesProdutoBinding binding;

    private Produto produtoSelecionado;

    private final List<String> idsFavoritos = new ArrayList<>();

    private final List<Produto> produtosList = new ArrayList<>();

    private LojaProdutoAdapter lojaProdutoAdapter;

    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemDAO = new ItemDAO(this);
        itemPedidoDAO = new ItemPedidoDAO(this);

        configClicks();

        getExtra();

        recuperarFavoritos();

        configRvProdutos();
    }

    private void configClicks() {
        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });

        binding.include.textTitulo.setText("Detalhes do produto");

        binding.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if(FirebaseHelper.getAutenticado()) {
                    idsFavoritos.add(produtoSelecionado.getId());

                    Favorito.salvar(idsFavoritos);
                } else {
                    Toast.makeText(getBaseContext(), "Você não está autenticado no app.", Toast.LENGTH_SHORT).show();
                    binding.likeButton.setLiked(false);

                    // TODO: Criar dialog para levar o usuário à tela de login
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                idsFavoritos.remove(produtoSelecionado.getId());

                Favorito.salvar(idsFavoritos);
            }
        });

        binding.btnAddCarrinho.setOnClickListener(v -> {
            addCarrinho();
        });
    }

    private void addCarrinho() {
        ItemPedido itemPedido = new ItemPedido();
        itemPedido.setIdProduto(produtoSelecionado.getId());
        itemPedido.setQuantidade(1);
        itemPedido.setValor(produtoSelecionado.getValorAtual());

        itemPedidoDAO.salvar(itemPedido);

        itemDAO.salvar(produtoSelecionado);
    }

    private void configRvProdutos() {
        binding.rvProdutos.setLayoutManager(new GridLayoutManager(this, 1, LinearLayoutManager.HORIZONTAL, false));
        binding.rvProdutos.setHasFixedSize(true);

        lojaProdutoAdapter = new LojaProdutoAdapter(R.layout.item_produto_similar_adapter, produtosList, this, true, idsFavoritos,  this, this);

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

                    for(String categoria : produtoSelecionado.getIdsCategorias()) {
                        if(produto.getIdsCategorias().contains(categoria)) {
                            if(!produtosList.contains(produto) && !produto.getId().equals(produtoSelecionado.getId())) {
                                produtosList.add(produto);
                            }
                        }
                    }
                }

                Collections.reverse(produtosList);

                lojaProdutoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

                    binding.likeButton.setLiked(idsFavoritos.contains(produtoSelecionado.getId()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getExtra() {
        produtoSelecionado = (Produto) getIntent().getSerializableExtra("produtoSelecionado");

        configDados();

        recuperarProdutos();
    }

    private void configDados() {
        binding.sliderView.setSliderAdapter(new SliderAdapter(produtoSelecionado.getUrlsImagens()));
        binding.sliderView.startAutoCycle();
        binding.sliderView.setScrollTimeInSec(4);
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);

        binding.textProduto.setText(produtoSelecionado.getTitulo());
        binding.textDescricao.setText(produtoSelecionado.getDescricao());
        binding.textValor.setText(getString(R.string.valor, GetMask.getValor(produtoSelecionado.getValorAtual())));
    }

    @Override
    public void onClick(Produto produto) {
        Intent intent = new Intent(this, DetalhesProdutoActivity.class);
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