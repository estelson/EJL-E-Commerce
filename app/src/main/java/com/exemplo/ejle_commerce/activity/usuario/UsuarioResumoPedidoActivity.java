package com.exemplo.ejle_commerce.activity.usuario;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.dao.ItemDAO;
import com.exemplo.ejle_commerce.dao.ItemPedidoDAO;
import com.exemplo.ejle_commerce.databinding.ActivityUsuarioResumoPedidoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Endereco;
import com.exemplo.ejle_commerce.util.GetMask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioResumoPedidoActivity extends AppCompatActivity {

    private ActivityUsuarioResumoPedidoBinding binding;

    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    private List<Endereco> enderecoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsuarioResumoPedidoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recuperaEnderecos();
    }

    private void configDados() {
        itemDAO = new ItemDAO(this);
        itemPedidoDAO = new ItemPedidoDAO(this);

        binding.include.include.ibVoltar.setOnClickListener(v -> {
            finish();
        });

        binding.include.textTitulo.setText("Resumo do pedido");

        if(!enderecoList.isEmpty()) {
            Endereco endereco = enderecoList.get(0);

            StringBuilder enderecoCompleto = new StringBuilder();
            enderecoCompleto
                    .append(endereco.getLogradouro())
                    .append(", ")
                    .append(endereco.getNumero())
                    .append("\n")
                    .append(endereco.getBairro())
                    .append(", ")
                    .append(endereco.getLocalidade())
                    .append(" - ")
                    .append(endereco.getUf())
                    .append("\nCEP: ")
                    .append(endereco.getCep());

            binding.textEnderecoEntrega.setText(enderecoCompleto);
            binding.btnAlterarEndereco.setText("Alterar endereço de entrega");
        } else {
            binding.textEnderecoEntrega.setText("Nenhum endereço cadastrado");
            binding.btnAlterarEndereco.setText("Cadastrar endereço de entrega");
        }

        binding.textValorTotal.setText(getString(R.string.valor, GetMask.getValor(itemPedidoDAO.getTotalPedido())));
        binding.textValor.setText(getString(R.string.valor_total_carrinho, GetMask.getValor(itemPedidoDAO.getTotalPedido())));
    }

    private void recuperaEnderecos() {
        DatabaseReference enderecoRef = FirebaseHelper.getDatabaseReference()
                .child("enderecos")
                .child(FirebaseHelper.getIdFirebase());

        enderecoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    Endereco endereco = ds.getValue(Endereco.class);
                    enderecoList.add(endereco);
                }

                binding.progressBar.setVisibility(View.GONE);

                Collections.reverse(enderecoList);

                configDados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}