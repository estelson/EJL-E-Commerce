package com.exemplo.ejle_commerce.fragment.usuario;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.adapter.UsuarioPedidosAdapter;
import com.exemplo.ejle_commerce.databinding.FragmentUsuarioPedidoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioPedidoFragment extends Fragment implements UsuarioPedidosAdapter.OnClickListener {

    private FragmentUsuarioPedidoBinding binding;

    private UsuarioPedidosAdapter usuarioPedidosAdapter;

    private List<Pedido> pedidoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuarioPedidoBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        configRv();

        recuperarPedidos();
    }

    private void configRv() {
        binding.rvPedidos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPedidos.setHasFixedSize(true);

        usuarioPedidosAdapter = new UsuarioPedidosAdapter(pedidoList, requireContext(), this);

        binding.rvPedidos.setAdapter(usuarioPedidosAdapter);
    }

    private void recuperarPedidos() {
        DatabaseReference pedidoRef = FirebaseHelper.getDatabaseReference()
                .child("usuarioPedidos")
                .child(FirebaseHelper.getIdFirebase());

        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    pedidoList.clear();

                    for(DataSnapshot ds : snapshot.getChildren()) {
                        Pedido pedido = ds.getValue(Pedido.class);

                        pedidoList.add(pedido);
                    }

                    binding.textInfo.setVisibility(View.GONE);
                } else {
                    binding.textInfo.setText("Nenhum pedido encontrado");
                }

                binding.progressBar.setVisibility(View.GONE);

                Collections.reverse(pedidoList);

                usuarioPedidosAdapter.notifyDataSetChanged();
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
    public void onClick(Pedido pedido) {
        Toast.makeText(requireContext(), pedido.getPagamento(), Toast.LENGTH_SHORT).show();
    }

}