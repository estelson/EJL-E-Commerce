package com.exemplo.ejle_commerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.exemplo.ejle_commerce.model.Produto;
import com.exemplo.ejle_commerce.util.GetMask;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.text.DecimalFormat;
import java.util.List;

public class LojaProdutoAdapter extends RecyclerView.Adapter<LojaProdutoAdapter.MyViewHolder> {

    private int layout;
    private final List<Produto> produtosList;
    private final Context context;
    private final boolean favorito;
    private final List<String> idsFavoritos;
    private final OnClickListener onClickListener;
    private final OnClickFavorito onClickFavorito;

    public LojaProdutoAdapter(int layout, List<Produto> produtosList, Context context, boolean favorito, List<String> idsFavoritos, OnClickListener onClickListener, OnClickFavorito onClickFavorito) {
        this.layout = layout;
        this.produtosList = produtosList;
        this.context = context;
        this.favorito = favorito;
        this.idsFavoritos = idsFavoritos;
        this.onClickListener = onClickListener;
        this.onClickFavorito = onClickFavorito;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Produto produto = produtosList.get(position);

        holder.txtNomeProduto.setText(produto.getTitulo());

        if(favorito) {
            holder.likeButton.setLiked(idsFavoritos.contains(produto.getId()));
        } else {
            holder.likeButton.setVisibility(View.GONE);
        }

        if(produto.getValorAntigo() > 0 && produto.getValorAntigo() != produto.getValorAtual()) {
            double resto = produto.getValorAntigo() - produto.getValorAtual();
            double porcentagem = (resto / produto.getValorAntigo() * 100);

            DecimalFormat df = new DecimalFormat("##0.00");
            holder.txtDescontoProduto.setText(context.getString(R.string.valor_off, df.format(porcentagem), "%"));
        } else {
            holder.txtDescontoProduto.setVisibility(View.GONE);
        }

        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if(FirebaseHelper.getAutenticado()) {
                    onClickFavorito.onClickFavorito(produto);
                } else {
                    Toast.makeText(context, "Você não está autenticado no app.", Toast.LENGTH_SHORT).show();
                    holder.likeButton.setLiked(false);

                    // TODO: Criar dialog para levar o usuário à tela de login
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                onClickFavorito.onClickFavorito(produto);
            }
        });

        for (int i = 0; i < produto.getUrlsImagens().size(); i++) {
            if(produto.getUrlsImagens().get(i).getIndex() == 0) {
                Glide.with(context)
                        .load(produto.getUrlsImagens().get(i).getCaminhoImagem())
                        .into(holder.imagemProduto);
            }
        }

        holder.txtValorProduto.setText(context.getString(R.string.valor, GetMask.getValor(produto.getValorAtual())));

        holder.itemView.setOnClickListener(v -> {
            onClickListener.onClick(produto);
        });
    }

    @Override
    public int getItemCount() {
        return produtosList.size();
    }

    public interface OnClickListener {
        void onClick(Produto produto);
    }

    public interface OnClickFavorito {
        void onClickFavorito(Produto produto);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imagemProduto;

        LikeButton likeButton;

        TextView txtNomeProduto;
        TextView txtValorProduto;
        TextView txtDescontoProduto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagemProduto = itemView.findViewById(R.id.imagemProduto);

            likeButton = itemView.findViewById(R.id.likeButton);

            txtNomeProduto = itemView.findViewById(R.id.txtNomeProduto);
            txtValorProduto = itemView.findViewById(R.id.txtValorProduto);
            txtDescontoProduto = itemView.findViewById(R.id.txtDescontoProduto);
        }
    }

}
