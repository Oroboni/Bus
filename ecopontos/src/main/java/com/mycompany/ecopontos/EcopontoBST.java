package com.mycompany.ecopontos;

public class EcopontoBST {
    private NoBST raiz;

    private static class NoBST {
        PontoColeta ponto;
        NoBST esquerda;
        NoBST direita;

        public NoBST(PontoColeta ponto) {
            this.ponto = ponto;
            this.esquerda = null;
            this.direita = null;
        }
    }

    public void inserir(PontoColeta ponto) {
        raiz = inserirRecursivo(raiz, ponto);
    }

    private NoBST inserirRecursivo(NoBST atual, PontoColeta ponto) {
        if (atual == null) {
            return new NoBST(ponto);
        }

        int comparacao = ponto.getNomeLocal().compareToIgnoreCase(atual.ponto.getNomeLocal());
        if (comparacao < 0) {
            atual.esquerda = inserirRecursivo(atual.esquerda, ponto);
        } else if (comparacao > 0) {
            atual.direita = inserirRecursivo(atual.direita, ponto);
        } else {
            // Ponto com o mesmo nome já existe (opcional: lidar com duplicatas)
            // Para simplificar, não faremos nada se for exatamente igual
        }
        return atual;
    }

    public PontoColeta buscarPorNome(String nome) {
        return buscarRecursivo(raiz, nome);
    }

    private PontoColeta buscarRecursivo(NoBST atual, String nome) {
        if (atual == null) {
            return null; // Não encontrado
        }

        int comparacao = nome.compareToIgnoreCase(atual.ponto.getNomeLocal());
        if (comparacao == 0) {
            return atual.ponto; // Encontrado
        } else if (comparacao < 0) {
            return buscarRecursivo(atual.esquerda, nome);
        } else {
            return buscarRecursivo(atual.direita, nome);
        }
    }

    public void exibirEmOrdem() {
        System.out.println("\n--- Pontos de Coleta (Ordem Alfabética por Nome - BST) ---");
        exibirEmOrdemRecursivo(raiz);
    }

    private void exibirEmOrdemRecursivo(NoBST no) {
        if (no != null) {
            exibirEmOrdemRecursivo(no.esquerda);
            System.out.println("  " + no.ponto);
            exibirEmOrdemRecursivo(no.direita);
        }
    }
}