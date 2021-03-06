package com.example.guiro.togeather.model;

import com.example.guiro.togeather.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Requisicao {
    private String id;
    private String status;
    private Usuario usuario;
    private Usuario acompanhante;
    private Destino destino;

    public static final String STATUS_AGUARDANDO = "AGUARDANDO";
    public static final String STATUS_A_CAMINHO = "ACAMINHO";
    public static final String STATUS_VIAGEM  = "VIAGEM";
    public static final String STATUS_FINALIZADA = "FINALIZADA";
    public static final String STATUS_CANCELADA = "CANCELADA";

    public Requisicao() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        String idRequisicao = requisicoes.push().getKey();
        setId(idRequisicao);

        requisicoes.child( getId()).setValue(this);
    }

    public void atualizarStatus(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        DatabaseReference requisicao = requisicoes.child(getId());

        Map objeto = new HashMap();
        objeto.put("status", getStatus());

        requisicao.updateChildren( objeto );

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getAcompanhante() {
        return acompanhante;
    }

    public void setAcompanhante(Usuario acompanhante) {
        this.acompanhante = acompanhante;
    }

    public Destino getDestino() {
        return destino;
    }

    public void setDestino(Destino destino) {
        this.destino = destino;
    }
}
