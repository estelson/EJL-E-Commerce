package com.exemplo.ejle_commerce.model;

import com.exemplo.ejle_commerce.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

public class Categoria {

    private String id;
    private String nome;
    private String urlImagem;
    private boolean todas = false;

    public Categoria() {
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference();
        this.setId(categoriaRef.push().getKey());
    }

    public void Salvar() {
        DatabaseReference categoriaRef = FirebaseHelper.getDatabaseReference()
                .child("categorias")
                .child(this.getId());

        categoriaRef.setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public boolean isTodas() {
        return todas;
    }

    public void setTodas(boolean todas) {
        this.todas = todas;
    }

}
