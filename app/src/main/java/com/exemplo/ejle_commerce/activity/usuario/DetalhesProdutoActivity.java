package com.exemplo.ejle_commerce.activity.usuario;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.adapter.SliderAdapter;
import com.exemplo.ejle_commerce.databinding.ActivityDetalhesProdutoBinding;
import com.exemplo.ejle_commerce.model.Produto;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;

public class DetalhesProdutoActivity extends AppCompatActivity {

    private ActivityDetalhesProdutoBinding binding;

    private Produto produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetalhesProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getExtra();
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
    }

}