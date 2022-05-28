package com.exemplo.ejle_commerce.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.exemplo.ejle_commerce.R;
import com.exemplo.ejle_commerce.model.Pedido;
import com.exemplo.ejle_commerce.model.StatusPedido;
import com.exemplo.ejle_commerce.util.GetMask;

import java.util.List;

public class UsuarioPedidosAdapter extends RecyclerView.Adapter<UsuarioPedidosAdapter.MyViewHolder> {

    private final List<Pedido> pedidoList;

    private final Context context;

    private final OnClickListener clickListener;

    public UsuarioPedidosAdapter(List<Pedido> pedidoList, Context context, OnClickListener clickListener) {
        this.pedidoList = pedidoList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_pedido_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pedido pedido = pedidoList.get(position);

        holder.textIdPedido.setText(pedido.getId());
        holder.textTotalPedido.setText(context.getString(R.string.valor, GetMask.getValor(pedido.getTotal())));
        holder.textDataPedido.setText(GetMask.getDate(pedido.getDataPedido(), 2));

        holder.btnDetalhesPedido.setOnClickListener(v -> {
            clickListener.onClick(pedido);
        });

        switch (pedido.getStatusPedido()) {
            case PENDENTE:
                holder.textStatusPedido.setTextColor(ContextCompat.getColor(context, R.color.color_status_pedido_pendente));
                break;
            case APROVADO:
                holder.textStatusPedido.setTextColor(ContextCompat.getColor(context, R.color.color_status_pedido_aprovado));
                break;
            case CANCELADO:
                holder.textStatusPedido.setTextColor(ContextCompat.getColor(context, R.color.color_status_pedido_cancelado));
                break;
        }

        holder.textStatusPedido.setText(StatusPedido.getStatus(pedido.getStatusPedido()));
    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    public interface OnClickListener {
        void onClick(Pedido pedido);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textIdPedido;
        TextView textStatusPedido;
        TextView textTotalPedido;
        TextView textDataPedido;

        Button btnDetalhesPedido;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textIdPedido = itemView.findViewById(R.id.textIdPedido);
            textStatusPedido = itemView.findViewById(R.id.textStatusPedido);
            textTotalPedido = itemView.findViewById(R.id.textTotalPedido);
            textDataPedido = itemView.findViewById(R.id.textDataPedido);

            btnDetalhesPedido = itemView.findViewById(R.id.btnDetalhesPedido);
        }
    }

}