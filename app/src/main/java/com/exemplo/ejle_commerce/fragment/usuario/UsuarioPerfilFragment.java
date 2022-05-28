package com.exemplo.ejle_commerce.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.exemplo.ejle_commerce.activity.usuario.MainActivityUsuario;
import com.exemplo.ejle_commerce.activity.usuario.UsuarioEnderecoActivity;
import com.exemplo.ejle_commerce.activity.usuario.UsuarioPerfilActivity;
import com.exemplo.ejle_commerce.autenticacao.CadastroActivity;
import com.exemplo.ejle_commerce.autenticacao.LoginActivity;
import com.exemplo.ejle_commerce.databinding.FragmentUsuarioPerfilBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;

public class UsuarioPerfilFragment extends Fragment {

    private FragmentUsuarioPerfilBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuarioPerfilBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configClicks();
    }

    @Override
    public void onStart() {
        super.onStart();

        configMenu();
    }

    private void configClicks() {
        binding.btnEntrar.setOnClickListener(v -> {
            startActivity(LoginActivity.class);
        });

        binding.btnCadastrar.setOnClickListener(v -> {
            startActivity(CadastroActivity.class);
        });

        binding.btnMeusDados.setOnClickListener(v -> {
            startActivity(UsuarioPerfilActivity.class);
        });

        binding.btnEnderecos.setOnClickListener(v -> {
            startActivity(UsuarioEnderecoActivity.class);
        });

        binding.btnSair.setOnClickListener(v -> {
            FirebaseHelper.getAuth().signOut();

            requireActivity().finish();

            startActivity(new Intent(requireContext(), MainActivityUsuario.class));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //binding = null;
    }

    private void startActivity(Class<?> clazz) {
        if(FirebaseHelper.getAutenticado()) {
            startActivity(new Intent(requireContext(), clazz));
        } else {
            startActivity(new Intent(requireContext(), LoginActivity.class));
        }
    }

    private void configMenu() {
        if(FirebaseHelper.getAutenticado()) {
            binding.llLogado.setVisibility(View.GONE);
            binding.divisor.setVisibility(View.GONE);

            binding.btnSair.setVisibility(View.VISIBLE);
            binding.divisor3.setVisibility(View.VISIBLE);
        } else {
            binding.llLogado.setVisibility(View.VISIBLE);
            binding.divisor.setVisibility(View.VISIBLE);

            binding.btnSair.setVisibility(View.GONE);
            binding.divisor3.setVisibility(View.GONE);

        }
    }

}