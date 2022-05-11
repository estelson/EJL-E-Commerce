package com.exemplo.ejle_commerce.fragment.loja;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.databinding.DialogFormCategoriaBinding;
import com.exemplo.ejle_commerce.databinding.FragmentLojaCategoriaBinding;

public class LojaCategoriaFragment extends Fragment {

    private FragmentLojaCategoriaBinding binding;

    private AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLojaCategoriaBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configClicks();
    }

    private void configClicks() {
        binding.btnAddCategoria.setOnClickListener(v -> {
            showDialog();
        });
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);

        DialogFormCategoriaBinding categoriaBinding = DialogFormCategoriaBinding.inflate(LayoutInflater.from(getContext()));
        builder.setView(categoriaBinding.getRoot());

        categoriaBinding.btnFechar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        categoriaBinding.btnSalvar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //binding = null;
    }
}