package com.mycompany.ecopontos;

public class PontoColeta {
    private int id;
    private String nomeLocal;

    public PontoColeta(int id, String nomeLocal) {
        this.id = id;
        this.nomeLocal = nomeLocal;
    }

    public int getId() {
        return id;
    }

    public String getNomeLocal() {
        return nomeLocal;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Nome: " + nomeLocal;
    }
}
