package com.diegol.whatsappclone.whatsappclone.Model;

import com.diegol.whatsappclone.whatsappclone.Config.ConfiguracaoFIrebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String senha;

    public Usuario() {
        //construtor vazio

    }

    public void salvar(){
        DatabaseReference referenciaFirebase = ConfiguracaoFIrebase.getFirebase();
        referenciaFirebase.child("usuarios").child(getId()).setValue(this);//cria um dado no DB
    }

    @Exclude //anotação para o Firebase não salvar o ID no database, pois já tem no cadastro de email
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude //anotação para o Firebase não salvar a senha no database, pois já tem no cadastro de email
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
