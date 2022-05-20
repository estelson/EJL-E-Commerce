package com.exemplo.ejle_commerce.fragment.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.activity.loja.LojaFormProdutoActivity;
import com.exemplo.ejle_commerce.adapter.CarrinhoAdapter;
import com.exemplo.ejle_commerce.dao.ItemDAO;
import com.exemplo.ejle_commerce.dao.ItemPedidoDAO;
import com.exemplo.ejle_commerce.databinding.DialogLojaProdutoBinding;
import com.exemplo.ejle_commerce.databinding.DialogRemoverCarrinhoBinding;
import com.exemplo.ejle_commerce.databinding.FragmentUsuarioCarrinhoBinding;
import com.exemplo.ejle_commerce.model.ItemPedido;
import com.exemplo.ejle_commerce.model.Produto;
import com.exemplo.ejle_commerce.util.GetMask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsuarioCarrinhoFragment extends Fragment implements CarrinhoAdapter.OnClick {

    private FragmentUsuarioCarrinhoBinding binding;

    private List<ItemPedido> itemPedidoList = new ArrayList<>();

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
    }

    private void configRv() {
        Collections.reverse(itemPedidoList);

        binding.rvProdutos.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvProdutos.setHasFixedSize(true);

        carrinhoAdapter = new CarrinhoAdapter(itemPedidoList, itemPedidoDAO, requireContext(), this);
        binding.rvProdutos.setAdapter(carrinhoAdapter);

        configTotalCarrinho();
    }

    @Override
    public void onStart() {
        super.onStart();

        configInfo();
    }

    private void configTotalCarrinho() {
        binding.textValor.setText(getString(R.string.valor_total_carrinho, GetMask.getValor(itemPedidoDAO.getTotalCarrinho())));
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