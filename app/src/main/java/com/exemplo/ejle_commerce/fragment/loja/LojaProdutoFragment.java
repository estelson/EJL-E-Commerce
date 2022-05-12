package com.exemplo.ejle_commerce.fragment.loja;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.exemplo.ejle_commerce.activity.loja.LojaFormProdutoActivity;
import com.exemplo.ejle_commerce.databinding.FragmentLojaProdutoBinding;

public class LojaProdutoFragment extends Fragment {

    private FragmentLojaProdutoBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        binding = FragmentLojaProdutoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configClicks();
    }

    private void configClicks() {
        binding.toolbar.btnAdd.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), LojaFormProdutoActivity.class));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //binding = null;
    }

}