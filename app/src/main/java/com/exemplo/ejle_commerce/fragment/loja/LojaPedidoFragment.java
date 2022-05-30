package com.exemplo.ejle_commerce.fragment.loja;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.activity.app.DetalhesPedidoActivity;
import com.exemplo.ejle_commerce.adapter.LojaPedidosAdapter;
import com.exemplo.ejle_commerce.databinding.DialogDeleteBinding;
import com.exemplo.ejle_commerce.databinding.FragmentLojaPedidoBinding;
import com.exemplo.ejle_commerce.databinding.LayoutDialogStatusPedidoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Categoria;
import com.exemplo.ejle_commerce.model.Pedido;
import com.exemplo.ejle_commerce.model.StatusPedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LojaPedidoFragment extends Fragment implements LojaPedidosAdapter.OnClickListener {

    private FragmentLojaPedidoBinding binding;

    private LojaPedidosAdapter lojaPedidosAdapter;

    private final List<Pedido> pedidoList = new ArrayList<>();

    private AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLojaPedidoBinding.inflate(inflater, container, false);

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

        lojaPedidosAdapter = new LojaPedidosAdapter(pedidoList, requireContext(), this);

        binding.rvPedidos.setAdapter(lojaPedidosAdapter);
    }

    private void recuperarPedidos() {
        DatabaseReference pedidoRef = FirebaseHelper.getDatabaseReference()
                .child("lojaPedidos");

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
                    binding.textInfo.setText("Nenhum pedido recebido");
                }

                binding.progressBar.setVisibility(View.GONE);

                Collections.reverse(pedidoList);

                lojaPedidosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialogStatus(Pedido pedido) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog2);

        LayoutDialogStatusPedidoBinding statusBinding = LayoutDialogStatusPedidoBinding.inflate(LayoutInflater.from(getContext()));

        RadioGroup rgStatus = statusBinding.rgStatus;
        RadioButton rbPendente = statusBinding.rbPendente;
        RadioButton rbAprovado = statusBinding.rbAprovado;
        RadioButton rbCancelado = statusBinding.rbCancelado;

        switch (pedido.getStatusPedido()) {
            case PENDENTE:
                rgStatus.check(R.id.rbPendente);

                rbAprovado.setEnabled(true);
                rbCancelado.setEnabled(true);

                break;
            case APROVADO:
                rgStatus.check(R.id.rbAprovado);

                rbCancelado.setEnabled(false);
                rbPendente.setEnabled(false);

                break;
            case CANCELADO:
                rgStatus.check(R.id.rbCancelado);

                rbPendente.setEnabled(false);
                rbAprovado.setEnabled(false);

                break;
        }

        statusBinding.btnFechar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        statusBinding.btnConfirmar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        builder.setView(statusBinding.getRoot());

        dialog = builder.create();

        if(!requireActivity().isFinishing()) {
            dialog.show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public void onClick(Pedido pedido, String operacao) {
        switch (operacao) {
            case "detalhes":
                Intent intent = new Intent(requireContext(), DetalhesPedidoActivity.class);
                intent.putExtra("pedidoSelecionado", pedido);

                startActivity(intent);

                break;
            case "status":
                showDialogStatus(pedido);
                break;
            default:
                Toast.makeText(requireContext(), "Operação inválida. Favor verificar", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}