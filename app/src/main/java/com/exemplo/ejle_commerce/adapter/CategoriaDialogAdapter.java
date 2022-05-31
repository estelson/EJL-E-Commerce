package com.exemplo.ejle_commerce.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.model.Categoria;

import java.util.List;

public class CategoriaDialogAdapter extends RecyclerView.Adapter<CategoriaDialogAdapter.MyViewHolder> {

    private final List<String> idCategoriasSelecionadas;
    private final List<Categoria> categoriasList;
    private final Context context;
    private final OnClick onClick;

    public CategoriaDialogAdapter(List<String> idCategoriasSelecionadas, List<Categoria> categoriasList, Context context, OnClick onClick) {
        this.idCategoriasSelecionadas = idCategoriasSelecionadas;
        this.categoriasList = categoriasList;
        this.context = context;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria_dialog, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Categoria categoria = categoriasList.get(position);

        holder.nomeCategoria.setText(categoria.getNome());

        Glide.with(context)
                .load(categoria.getUrlImagem())
                .into(holder.imagemCategoria);

        if(idCategoriasSelecionadas.contains(categoria.getId())) {
            holder.checkBox.setChecked(true);
        }

        holder.itemView.setOnClickListener(v -> {
            onClick.onClickListener(categoria);

            holder.checkBox.setChecked(!holder.checkBox.isChecked());
        });
    }

    @Override
    public int getItemCount() {
        return categoriasList.size();
    }

    public interface OnClick {
        void onClickListener(Categoria categoria);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imagemCategoria;
        TextView nomeCategoria;
        CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imagemCategoria = itemView.findViewById(R.id.imagemCategoria);
            nomeCategoria = itemView.findViewById(R.id.nomeCategoria);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

}
