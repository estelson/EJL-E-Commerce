package com.exemplo.ejle_commerce.fragment.usuario;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.activity.loja.LojaFormProdutoActivity;
import com.exemplo.ejle_commerce.activity.usuario.UsuarioResumoPedidoActivity;
import com.exemplo.ejle_commerce.activity.usuario.UsuarioSelecionaPagamentoActivity;
import com.exemplo.ejle_commerce.adapter.CarrinhoAdapter;
import com.exemplo.ejle_commerce.autenticacao.LoginActivity;
import com.exemplo.ejle_commerce.dao.ItemDAO;
import com.exemplo.ejle_commerce.dao.ItemPedidoDAO;
import com.exemplo.ejle_commerce.databinding.DialogLojaProdutoBinding;
import com.exemplo.ejle_commerce.databinding.DialogRemoverCarrinhoBinding;
import com.exemplo.ejle_commerce.databinding.FragmentUsuarioCarrinhoBinding;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Favorito;
import com.exemplo.ejle_commerce.model.ItemPedido;
import com.exemplo.ejle_commerce.model.Produto;
import com.exemplo.ejle_commerce.model.Usuario;
import com.exemplo.ejle_commerce.util.GetMask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioCarrinhoFragment extends Fragment implements CarrinhoAdapter.OnClick {

    private FragmentUsuarioCarrinhoBinding binding;

    private final List<ItemPedido> itemPedidoList = new ArrayList<>();

    private final List<String> idsFavoritos = new ArrayList<>();

    private CarrinhoAdapter carrinhoAdapter;

    private ItemDAO itemDAO;
    private ItemPedidoDAO itemPedidoDAO;

    private AlertDialog dialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuarioCarrinhoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemDAO = new ItemDAO(requireContext());
        itemPedidoDAO = new ItemPedidoDAO(requireContext());
        itemPedidoList.addAll(itemPedidoDAO.getList());

        configRv();

        recuperarFavoritos();

        configClicks();
    }

    @Override
    public void onStart() {
        super.onStart();

        configInfo();
    }

    private void configClicks() {
        binding.btnContinuar.setOnClickListener(v -> {
            if(FirebaseHelper.getAutenticado()) {
                startActivity(new Intent(requireContext(), UsuarioSelecionaPagamentoActivity.class));
            } else {
                resultLauncher.launch(new Intent(requireContext(), LoginActivity.class));
            }
        });
    }

    private void configRv() {
        Collections.reverse(itemPedidoList);

        binding.rvProdutos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvProdutos.setHasFixedSize(true);

        carrinhoAdapter = new CarrinhoAdapter(itemPedidoList, itemPedidoDAO, requireContext(), this);
        binding.rvProdutos.setAdapter(carrinhoAdapter);

        configTotalCarrinho();
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void configTotalCarrinho() {
        binding.textValor.setText(getString(R.string.valor_total_carrinho, GetMask.getValor(itemPedidoDAO.getTotalPedido())));
    }

    private void configQtdProduto(int position, String operacao) {
        ItemPedido itemPedido = itemPedidoList.get(position);

        if(operacao.equals("mais")) { // +
            itemPedido.setQuantidade(itemPedido.getQuantidade() + 1);

            itemPedidoDAO.atualizar(itemPedido);

            itemPedidoList.set(position, itemPedido);
        } else { // -
            if (itemPedido.getQuantidade() > 1) {
                itemPedido.setQuantidade(itemPedido.getQuantidade() - 1);

                itemPedidoDAO.atualizar(itemPedido);

                itemPedidoList.set(position, itemPedido);
            }
        }

        carrinhoAdapter.notifyDataSetChanged();

        configTotalCarrinho();
    }

    private void showDialogRemover(Produto produto, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog);

        DialogRemoverCarrinhoBinding dialogBinding = DialogRemoverCarrinhoBinding.inflate(LayoutInflater.from(requireContext()));

        dialogBinding.likeButton.setLiked(idsFavoritos.contains(produto.getId()));

        dialogBinding.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if(FirebaseHelper.getAutenticado()) {
                    salvarFavorito(produto);
                } else {
                    Toast.makeText(requireContext(), "Você não está autenticado no app.", Toast.LENGTH_SHORT).show();
                    dialogBinding.likeButton.setLiked(false);

                    // TODO: Criar dialog para levar o usuário à tela de login
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                salvarFavorito(produto);
            }
        });

        Picasso.get().load(produto.getUrlsImagens().get(0).getCaminhoImagem()).into(dialogBinding.imagemProduto);

        dialogBinding.txtNomeProduto.setText(produto.getTitulo());

        dialogBinding.btnCancelar.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogBinding.btnAddFavorito.setOnClickListener(v -> {


            dialog.dismiss();
        });

        dialogBinding.btnRemover.setOnClickListener(v -> {
            removerProdutoCarrinho(position);

            dialog.dismiss();

            Toast.makeText(requireContext(), "Produto removido com sucesso", Toast.LENGTH_SHORT).show();
        });

        builder.setView(dialogBinding.getRoot());

        dialog = builder.create();
        dialog.show();
    }

    private void salvarFavorito(Produto produto) {
        if(!idsFavoritos.contains(produto.getId())) {
            idsFavoritos.add(produto.getId());
        } else {
            idsFavoritos.remove(produto.getId());
        }

        Favorito.salvar(idsFavoritos);
    }

    private void removerProdutoCarrinho(int position) {
        ItemPedido itemPedido = itemPedidoList.get(position);

        itemPedidoList.remove(itemPedido);

        itemPedidoDAO.remover(itemPedido);
        itemDAO.remover(itemPedido);

        carrinhoAdapter.notifyDataSetChanged();

        configInfo();

        configTotalCarrinho();
    }

    private void configInfo() {
        if(itemPedidoList.isEmpty()) {
            binding.textInfo.setVisibility(View.VISIBLE);
        } else {
            binding.textInfo.setVisibility(View.GONE);
        }
    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    startActivity(new Intent(requireContext(), UsuarioSelecionaPagamentoActivity.class));
                }
            }
    );

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    @Override
    public void onClickListener(int position, String operacao) {
        int idProduto = itemPedidoList.get(position).getId();
        Produto produto = itemPedidoDAO.getProduto(idProduto);

        switch (operacao) {
            case "detalhe":
                break;
            case "remover":
                showDialogRemover(produto, position);

                break;
            case "menos":
            case "mais":
                configQtdProduto(position, operacao);
                break;
        }
    }

}