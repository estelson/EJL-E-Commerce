package com.exemplo.ejle_commerce.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.model.Categoria;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.MyViewHolder> {

    private List<Categoria> categoriasList;

    private OnClick onClick;

    public CategoriaAdapter(List<Categoria> categoriasList, OnClick onClick) {
        this.categoriasList = categoriasList;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria_horizontal, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Categoria categoria = categoriasList.get(position);

        holder.itemView.setOnClickListener(v -> {
            onClick.onClickListener(categoria);
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
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ImageView imagemCategoria = itemView.findViewById(R.id.imagemCategoria);
            TextView nomeCategoria = itemView.findViewById(R.id.nomeCategoria);
        }
    }

}
