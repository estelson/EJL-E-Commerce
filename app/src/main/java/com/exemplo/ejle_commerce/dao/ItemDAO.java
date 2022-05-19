package com.exemplo.ejle_commerce.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.exemplo.ejle_commerce.model.Produto;

public class ItemDAO {

    private final SQLiteDatabase write;
    private final SQLiteDatabase read;

    public ItemDAO(Context context) {
        DBHelper dbHelper = new DBHelper(context);

        write = dbHelper.getWritableDatabase();
        read = dbHelper.getReadableDatabase();
    }

    public long salvar(Produto produto) {
        long idRetorno = 0;

        ContentValues values = new ContentValues();
        values.put("id_firebase", produto.getId());
        values.put("nome", produto.getTitulo());
        values.put("valor", produto.getValorAtual());

        for (int i = 0; i < produto.getUrlsImagens().size(); i++) {
            if(produto.getUrlsImagens().get(i).getIndex() == 0) {
                values.put("url_imagem", produto.getUrlsImagens().get(i).getCaminhoImagem());
            }
        }

        try {
            idRetorno = write.insert(DBHelper.TABELA_ITEM, null, values);
        } catch(Exception e) {
            Log.i("INFODB", "Erro ao salvar o produto. Motivo: " + e.getMessage());
        }

        return idRetorno;
    }
}
