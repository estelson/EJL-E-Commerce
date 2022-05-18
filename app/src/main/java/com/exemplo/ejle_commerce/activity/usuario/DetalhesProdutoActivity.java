package com.exemplo.ejle_commerce.activity.usuario;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.adapter.SliderAdapter;
import com.exemplo.ejle_commerce.databinding.ActivityDetalhesProdutoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Favorito;
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
import java.util.List;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private ActivityDetalhesProdutoBinding binding;

    private Produto produto;

    private final List<String> idsFavoritos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configClicks();

        getExtra();

        recuperarFavoritos();
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
                    idsFavoritos.add(produto.getId());

                    Favorito.salvar(idsFavoritos);
                } else {
                    Toast.makeText(getBaseContext(), "Você não está autenticado no app.", Toast.LENGTH_SHORT).show();
                    binding.likeButton.setLiked(false);

                    // TODO: Criar dialog para levar o usuário à tela de login
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                idsFavoritos.remove(produto.getId());

                Favorito.salvar(idsFavoritos);
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

                    binding.likeButton.setLiked(idsFavoritos.contains(produto.getId()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void getExtra() {
        produto = (Produto) getIntent().getSerializableExtra("produtoSelecionado");

        configDados();
    }

    private void configDados() {
        binding.sliderView.setSliderAdapter(new SliderAdapter(produto.getUrlsImagens()));
        binding.sliderView.startAutoCycle();
        binding.sliderView.setScrollTimeInSec(4);
        binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM);
        binding.sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);

        binding.textProduto.setText(produto.getTitulo());
        binding.textDescricao.setText(produto.getDescricao());
        binding.textValor.setText(getString(R.string.valor, GetMask.getValor(produto.getValorAtual())));
    }

}