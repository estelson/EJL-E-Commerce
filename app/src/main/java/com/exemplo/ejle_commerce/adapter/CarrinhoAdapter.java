package com.exemplo.ejle_commerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.dao.ItemPedidoDAO;
import com.exemplo.ejle_commerce.model.ItemPedido;
import com.exemplo.ejle_commerce.model.Produto;
import com.exemplo.ejle_commerce.util.GetMask;

import java.util.List;

public class CarrinhoAdapter extends RecyclerView.Adapter<CarrinhoAdapter.MyViewHolder> {

    private final List<ItemPedido> itensPedidoList;
    private final ItemPedidoDAO itemPedidoDAO;
    private final Context context;
    private final OnClick onClick;

    public CarrinhoAdapter(List<ItemPedido> itensPedidoList, ItemPedidoDAO itemPedidoDAO, Context context, OnClick onClick) {
        this.itensPedidoList = itensPedidoList;
        this.itemPedidoDAO = itemPedidoDAO;
        this.context = context;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter_carrinho, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemPedido itemPedido = itensPedidoList.get(position);
        Produto produto = itemPedidoDAO.getProduto(itemPedido.getId());

        holder.textTitulo.setText(produto.getTitulo());
        holder.textQuantidade.setText(String.valueOf(itemPedido.getQuantidade()));
        holder.textValor.setText(context.getString(R.string.valor, GetMask.getValor(itemPedido.getValor() * itemPedido.getQuantidade())));

        Glide.with(context)
                .load(produto.getUrlsImagens().get(0).getCaminhoImagem())
                .into(holder.imgProduto);

        holder.itemView.setOnClickListener(v -> {
            onClick.onClickListener(position, "detalhe");
        });

        holder.imgRemover.setOnClickListener(v -> {
            onClick.onClickListener(position, "remover");
        });

        holder.ibMenos.setOnClickListener(v -> {
            onClick.onClickListener(position, "menos");
        });

        holder.ibMais.setOnClickListener(v -> {
            onClick.onClickListener(position, "mais");
        });
    }

    @Override
    public int getItemCount() {
        return itensPedidoList.size();
    }

    public interface OnClick {
        public void onClickListener(int position, String operacao);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduto;
        ImageView imgRemover;

        TextView textTitulo;
        TextView textValor;

        ImageButton ibMenos;
        TextView textQuantidade;
        ImageButton ibMais;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduto = itemView.findViewById(R.id.imgProduto);
            imgRemover = itemView.findViewById(R.id.imgRemover);
            textTitulo = itemView.findViewById(R.id.textTitulo);
            textValor = itemView.findViewById(R.id.textValor);
            ibMenos = itemView.findViewById(R.id.ibMenos);
            textQuantidade = itemView.findViewById(R.id.textQuantidade);
            ibMais = itemView.findViewById(R.id.ibMais);
        }
    }

}
