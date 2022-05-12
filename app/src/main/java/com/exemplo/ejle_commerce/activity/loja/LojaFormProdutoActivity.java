package com.exemplo.ejle_commerce.activity.loja;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.databinding.ActivityLojaFormProdutoBinding;
import com.exemplo.ejle_commerce.databinding.BottomSheetFormProdutoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class LojaFormProdutoActivity extends AppCompatActivity {

    private ActivityLojaFormProdutoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLojaFormProdutoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        configClicks();
    }

    private void configClicks() {
        binding.imagemProduto0.setOnClickListener(v -> {
            showBottomSheet();
        });

        binding.imagemProduto1.setOnClickListener(v -> {
            showBottomSheet();
        });

        binding.imagemProduto2.setOnClickListener(v -> {
            showBottomSheet();
        });
    }

    private void showBottomSheet() {
        BottomSheetFormProdutoBinding sheetBinding = BottomSheetFormProdutoBinding.inflate(LayoutInflater.from(this));
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        bottomSheetDialog.setContentView(sheetBinding.getRoot());

        bottomSheetDialog.show();

        sheetBinding.btnCamera.setOnClickListener(v -> {
            Toast.makeText(this, "Câmera", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        sheetBinding.btnGaleria.setOnClickListener(v -> {
            Toast.makeText(this, "Galeria", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        sheetBinding.btnCancelar.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
    }

}