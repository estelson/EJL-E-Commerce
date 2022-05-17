package com.exemplo.ejle_commerce.fragment.loja;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.exemplo.ejle_commerce.activity.loja.LojaConfigActivity;
import com.exemplo.ejle_commerce.activity.loja.LojaRecebimentosActivity;
import com.exemplo.ejle_commerce.activity.usuario.MainActivityUsuario;
import com.exemplo.ejle_commerce.databinding.FragmentLojaConfigBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;

public class LojaConfigFragment extends Fragment {

    private FragmentLojaConfigBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLojaConfigBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configClicks();
    }

    private void configClicks() {
        binding.btnConfigLoja.setOnClickListener(v -> {
            startActivity(LojaConfigActivity.class);
        });

        binding.btnPagamentos.setOnClickListener(v -> {
            startActivity(LojaRecebimentosActivity.class);
        });

        binding.btnSair.setOnClickListener(v -> {
            FirebaseHelper.getAuth().signOut();

            requireActivity().finish();
            
            startActivity(MainActivityUsuario.class);
        });
    }

    private void startActivity(Class<?> clazz) {
        startActivity(new Intent(requireContext(), clazz));
    }

}