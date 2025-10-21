package com.mycompany.ecopontos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EcopontoTrie {
    private NoTrie raiz;

    private static class NoTrie {
        Map<Character, NoTrie> filhos;
        PontoColeta pontoFinal; // Armazena o ponto se for o fim de uma palavra

        public NoTrie() {
            filhos = new HashMap<>();
            pontoFinal = null;
        }
    }

    public EcopontoTrie() {
        raiz = new NoTrie();
    }

    public void inserir(PontoColeta ponto) {
        String nome = ponto.getNomeLocal().toLowerCase(); // Usar minúsculas para busca case-insensitive
        NoTrie atual = raiz;
        for (char c : nome.toCharArray()) {
            atual.filhos.putIfAbsent(c, new NoTrie());
            atual = atual.filhos.get(c);
        }
        atual.pontoFinal = ponto; // Marca o final da palavra com o ponto de coleta
    }

    public List<PontoColeta> buscarPorPrefixo(String prefixo) {
        List<PontoColeta> resultados = new ArrayList<>();
        String prefixoMinusculo = prefixo.toLowerCase();
        NoTrie atual = raiz;

        // Navega até o final do prefixo
        for (char c : prefixoMinusculo.toCharArray()) {
            NoTrie proximo = atual.filhos.get(c);
            if (proximo == null) {
                return resultados; // Nenhuma palavra começa com este prefixo
            }
            atual = proximo;
        }

        // A partir do nó do prefixo, coleta todos os pontos filhos
        coletarTodosPontos(atual, resultados);
        return resultados;
    }

    private void coletarTodosPontos(NoTrie no, List<PontoColeta> lista) {
        if (no == null) {
            return;
        }
        if (no.pontoFinal != null) {
            lista.add(no.pontoFinal);
        }
        for (NoTrie filho : no.filhos.values()) {
            coletarTodosPontos(filho, lista);
        }
    }
}