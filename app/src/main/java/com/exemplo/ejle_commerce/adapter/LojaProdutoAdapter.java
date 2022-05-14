package com.exemplo.ejle_commerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.model.Categoria;
import com.exemplo.ejle_commerce.model.Produto;
import com.squareup.picasso.Picasso;

import java.util.List;

public class LojaProdutoAdapter extends RecyclerView.Adapter<LojaProdutoAdapter.MyViewHolder> {

    private List<Produto> produtosList;

    private Context context;

    private OnClickListener onClickListener;

    public LojaProdutoAdapter(List<Produto> produtosList, Context context, OnClickListener onClickListener) {
        this.produtosList = produtosList;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = produtosList.get(position);

        holder.txtNomeProduto.setText(produto.getTitulo());

        for (int i = 0; i < produto.getUrlsImagens().size(); i++) {
            if(produto.getUrlsImagens().get(i).getIndex() == 0) {
                Picasso.get().load(produto.getUrlsImagens().get(i).getCaminhoImagem()).into(holder.imagemProduto);
            }
        }

        holder.txtValorProduto.setText(String.valueOf(produto.getValorAtual()));
        holder.txtDescontoProduto.setText(String.valueOf("15% OFF"));
    }

    @Override
    public int getItemCount() {
        return produtosList.size();
    }

    public interface OnClickListener {
        void onClick(Produto produto);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imagemProduto;

        TextView txtNomeProduto;
        TextView txtValorProduto;
        TextView txtDescontoProduto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagemProduto = itemView.findViewById(R.id.imagemProduto);

            txtNomeProduto = itemView.findViewById(R.id.txtNomeProduto);
            txtValorProduto = itemView.findViewById(R.id.txtValorProduto);
            txtDescontoProduto = itemView.findViewById(R.id.txtDescontoProduto);
        }
    }

}
