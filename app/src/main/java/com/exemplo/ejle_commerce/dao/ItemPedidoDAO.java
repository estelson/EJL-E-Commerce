package com.exemplo.ejle_commerce.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.exemplo.ejle_commerce.model.ImagemUpload;
import com.exemplo.ejle_commerce.model.ItemPedido;
import com.exemplo.ejle_commerce.model.Produto;

import java.util.ArrayList;
import java.util.List;

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

    public Produto getProduto(int idProduto) {
        Produto produto = null;
        List<ImagemUpload> uploadList = new ArrayList<>();

        String sql = "SELECT * FROM " + DBHelper.TABELA_ITEM + " WHERE id = " + idProduto + ";";
        Cursor cursor = read.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String id_firebase = cursor.getString(cursor.getColumnIndexOrThrow("id_firebase"));
            String titulo = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
            double valor = cursor.getDouble(cursor.getColumnIndexOrThrow("valor"));
            String url_imagem = cursor.getString(cursor.getColumnIndexOrThrow("url_imagem"));

            produto = new Produto();
            produto.setIdLocal(id);
            produto.setId(id_firebase);
            produto.setTitulo(titulo);
            produto.setValorAtual(valor);

            uploadList.add(new ImagemUpload(0, url_imagem));
            produto.setUrlsImagens(uploadList);
        }

        cursor.close();

        return produto;
    }

    public List<ItemPedido> getList() {
        List<ItemPedido> itemPedidoList = new ArrayList<>();

        String sql = "SELECT * FROM " + DBHelper.TABELA_ITEM_PEDIDO + ";";
        Cursor cursor = read.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String id_produto = cursor.getString(cursor.getColumnIndexOrThrow("id_produto"));
            double valor = cursor.getDouble(cursor.getColumnIndexOrThrow("valor"));
            int quantidade = cursor.getInt(cursor.getColumnIndexOrThrow("quantidade"));

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setId(id);
            itemPedido.setIdProduto(id_produto);
            itemPedido.setValor(valor);
            itemPedido.setQuantidade(quantidade);

            itemPedidoList.add(itemPedido);
        }

        cursor.close();

        return itemPedidoList;
    }

    public Double getTotalCarrinho() {
        double total = 0;
        for(ItemPedido itemPedido : getList()) {
            total += itemPedido.getValor();
        }

        return total;
    }

}
