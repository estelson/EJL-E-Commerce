package com.exemplo.ejle_commerce.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.exemplo.ejle_commerce.model.ItemPedido;

public class ItemPedidoDAO {

    private final SQLiteDatabase write;
    private final SQLiteDatabase read;

    public ItemPedidoDAO(Context context) {
        DBHelper dbHelper = new DBHelper(context);

        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }

    public boolean salvar(ItemPedido itemPedido) {
        ContentValues values = new ContentValues();
        values.put("id_produto", itemPedido.getIdProduto());
        values.put("valor", itemPedido.getValor());
        values.put("quantidade", itemPedido.getQuantidade());

        try {
            write.insert(DBHelper.TABELA_ITEM_PEDIDO, null, values);
        } catch(Exception e) {
            Log.i("INFODB", "Erro ao salvar o ItemPedido. Motivo: " + e.getMessage());

            return false;
        }

        return true;
    }
}