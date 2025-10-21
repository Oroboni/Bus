package com.mycompany.ecopontos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class GrafoEcopontos {
    private int numVertices;
    public ArrayList<PontoColeta> pontos; // Mapeia id do grafo para o objeto PontoColeta
    public HashMap<Integer, Integer> idParaIndice; // Mapeia o ID real do ecoponto para o índice no grafo (0 a N-1)

    // Representações do grafo
    public int[][] matrizAdjacencia; // Armazena o peso da aresta
    public int[][] matrizIncidencia; // Linhas = vertices, Colunas = arestas. 1 se incidente, 0 caso contrario.
    public ArrayList<Aresta> listaArestas;
    public ArrayList<ArrayList<Integer>> listaSucessores; // Lista de adjacência (vizinhos)

    public GrafoEcopontos(int maxVertices) {
        this.numVertices = 0; // Começa com 0 vértices, adicionaremos dinamicamente
        this.pontos = new ArrayList<>();
        this.idParaIndice = new HashMap<>();

        // Inicializamos com um tamanho máximo para evitar redimensionamento constante
        // ou você pode redimensionar a cada adição. Para simplificar, vamos usar maxVertices
        this.matrizAdjacencia = new int[maxVertices][maxVertices];
        this.matrizIncidencia = new int[maxVertices][maxVertices]; // Colunas serão ajustadas dinamicamente
        this.listaArestas = new ArrayList<>();
        this.listaSucessores = new ArrayList<>();
        for (int i = 0; i < maxVertices; i++) {
            listaSucessores.add(new ArrayList<>());
        }
    }

    // Adiciona um ponto de coleta ao grafo
    public void adicionarPonto(PontoColeta ponto) {
        if (!idParaIndice.containsKey(ponto.getId())) {
            int indiceGrafo = numVertices;
            idParaIndice.put(ponto.getId(), indiceGrafo);
            pontos.add(ponto);
            numVertices++;
            // Se o numVertices exceder maxVertices, seria necessário redimensionar as matrizes
            // Para este projeto, assumimos que maxVertices é suficiente.
        }
    }

    // Retorna o objeto PontoColeta dado o ID real
    public PontoColeta getPontoColetaPorId(int id) {
        Integer indice = idParaIndice.get(id);
        if (indice != null && indice < pontos.size()) {
            return pontos.get(indice);
        }
        return null;
    }

    // Adiciona uma aresta entre dois pontos
    public void adicionarAresta(int idOrigem, int idDestino, int peso) {
        Integer u = idParaIndice.get(idOrigem);
        Integer v = idParaIndice.get(idDestino);

        if (u == null || v == null) {
            System.err.println("Erro: Ponto de origem ou destino não encontrado ao adicionar aresta.");
            return;
        }

        // Matriz de Adjacência
        matrizAdjacencia[u][v] = peso;
        matrizAdjacencia[v][u] = peso; // Grafo não direcionado

        // Lista de Arestas
        listaArestas.add(new Aresta(idOrigem, idDestino, peso));
        // A matriz de incidência é mais complexa de gerenciar dinamicamente sem saber o número total de arestas de antemão.
        // Para simplificar, a preencheremos de forma mais direta no final ou reestruturamos se necessário.
        // Por enquanto, vamos representá-la com 1s e 0s para as arestas existentes.
        // Se a matriz de incidência precisa armazenar o peso, seria um pouco diferente.
        // Para o nosso caso, vamos considerar 1 (existe) ou 0 (não existe) para a incidência.
        // Se for para armazenar o peso: -peso para origem, +peso para destino (grafo direcionado), ou só o peso.
        // Como é não direcionado e só para indicar incidência: 1 para ambos.
        // Ajustando a matriz de incidência para representar a aresta i na coluna k
        int numArestaAtual = listaArestas.size() - 1; // Índice da aresta recém-adicionada
        matrizIncidencia[u][numArestaAtual] = 1;
        matrizIncidencia[v][numArestaAtual] = 1;

        // Lista de Sucessores (Adjacência)
        listaSucessores.get(u).add(v);
        listaSucessores.get(v).add(u); // Grafo não direcionado
    }

    // --- Representações do Grafo ---

    public void exibirMatrizAdjacencia() {
        System.out.println("\n--- Matriz de Adjacência (Pesos) ---");
        System.out.print("   ");
        for (int i = 0; i < numVertices; i++) {
            System.out.printf("%4d", pontos.get(i).getId());
        }
        System.out.println();
        for (int i = 0; i < numVertices; i++) {
            System.out.printf("%2d ", pontos.get(i).getId());
            for (int j = 0; j < numVertices; j++) {
                System.out.printf("%4d", matrizAdjacencia[i][j]);
            }
            System.out.println();
        }
    }

    public void exibirMatrizIncidencia() {
        System.out.println("\n--- Matriz de Incidência ---");
        System.out.print("   ");
        for (int i = 0; i < listaArestas.size(); i++) {
            System.out.printf("A%d ", i + 1); // Exibe A1, A2, etc. para as arestas
        }
        System.out.println();
        for (int i = 0; i < numVertices; i++) {
            System.out.printf("%2d ", pontos.get(i).getId());
            for (int j = 0; j < listaArestas.size(); j++) {
                // A matriz de incidência indica se o vértice 'i' é incidente à aresta 'j'
                // Aqui estamos usando a coluna 'j' como o índice da aresta na listaArestas
                int u = idParaIndice.get(listaArestas.get(j).getOrigem());
                int v = idParaIndice.get(listaArestas.get(j).getDestino());

                if (i == u || i == v) {
                    System.out.printf("%3d", 1);
                } else {
                    System.out.printf("%3d", 0);
                }
            }
            System.out.println();
        }
    }

    public void exibirListaArestas() {
        System.out.println("\n--- Lista de Arestas ---");
        for (Aresta aresta : listaArestas) {
            PontoColeta origem = getPontoColetaPorId(aresta.getOrigem());
            PontoColeta destino = getPontoColetaPorId(aresta.getDestino());
            System.out.println("  " + origem.getNomeLocal() + " (" + origem.getId() + ") -- " + aresta.getPeso() + " --> " + destino.getNomeLocal() + " (" + destino.getId() + ")");
        }
    }

    public void exibirListaSucessores() {
        System.out.println("\n--- Lista de Sucessores (Adjacência) ---");
        for (int i = 0; i < numVertices; i++) {
            PontoColeta pontoAtual = pontos.get(i);
            System.out.print("  " + pontoAtual.getNomeLocal() + " (" + pontoAtual.getId() + "): ");
            for (int vizinhoIndice : listaSucessores.get(i)) {
                PontoColeta vizinho = pontos.get(vizinhoIndice);
                // Encontrar o peso da aresta entre pontoAtual e vizinho
                int peso = matrizAdjacencia[i][vizinhoIndice];
                System.out.print(vizinho.getNomeLocal() + " (" + vizinho.getId() + ") [Peso: " + peso + "], ");
            }
            System.out.println();
        }
    }

    // --- Operações sobre o Grafo ---

    public void calcularGrauTodosPontos() {
        System.out.println("\n--- Grau de Cada Ecoponto ---");
        for (int i = 0; i < numVertices; i++) {
            PontoColeta ponto = pontos.get(i);
            int grau = listaSucessores.get(i).size(); // Para grafo não direcionado, o tamanho da lista de sucessores é o grau
            System.out.println("  " + ponto.getNomeLocal() + " (ID: " + ponto.getId() + ") - Grau: " + grau);
        }
    }

    // BFS para caminho mais curto em grafos não ponderados (ou se consideramos todas as arestas com peso 1)
    public List<PontoColeta> bfsCaminhoMaisCurto(int idOrigem, int idDestino) {
        Integer indiceOrigem = idParaIndice.get(idOrigem);
        Integer indiceDestino = idParaIndice.get(idDestino);

        if (indiceOrigem == null || indiceDestino == null) {
            System.err.println("Erro: ID de origem ou destino inválido para BFS.");
            return null;
        }

        Queue<Integer> fila = new LinkedList<>();
        HashMap<Integer, Integer> pais = new HashMap<>(); // Guarda o pai para reconstruir o caminho
        HashSet<Integer> visitados = new HashSet<>();

        fila.offer(indiceOrigem);
        visitados.add(indiceOrigem);
        pais.put(indiceOrigem, null); // O ponto de origem não tem pai

        while (!fila.isEmpty()) {
            int atualIndice = fila.poll();

            if (atualIndice == indiceDestino) {
                return reconstruirCaminho(pais, indiceOrigem, indiceDestino);
            }

            for (int vizinhoIndice : listaSucessores.get(atualIndice)) {
                if (!visitados.contains(vizinhoIndice)) {
                    visitados.add(vizinhoIndice);
                    fila.offer(vizinhoIndice);
                    pais.put(vizinhoIndice, atualIndice);
                }
            }
        }
        return null; // Caminho não encontrado
    }

    // Dijkstra para caminho mais curto em grafos ponderados
    public List<PontoColeta> dijkstraCaminhoMaisCurto(int idOrigem, int idDestino) {
        Integer indiceOrigem = idParaIndice.get(idOrigem);
        Integer indiceDestino = idParaIndice.get(idDestino);

        if (indiceOrigem == null || indiceDestino == null) {
            System.err.println("Erro: ID de origem ou destino inválido para Dijkstra.");
            return null;
        }

        // Distâncias do início a cada nó
        HashMap<Integer, Integer> distancias = new HashMap<>();
        // Pais para reconstruir o caminho
        HashMap<Integer, Integer> pais = new HashMap<>();
        // Conjunto de nós já visitados e com distância final conhecida
        HashSet<Integer> visitados = new HashSet<>();
        // Fila de prioridade para pegar sempre o nó com menor distância
        PriorityQueue<NoDijkstra> pq = new PriorityQueue<>(Comparator.comparingInt(no -> no.distancia));

        // Inicializar distâncias: 0 para origem, infinito para outros
        for (int i = 0; i < numVertices; i++) {
            distancias.put(i, Integer.MAX_VALUE);
        }
        distancias.put(indiceOrigem, 0);

        pq.add(new NoDijkstra(indiceOrigem, 0));

        while (!pq.isEmpty()) {
            NoDijkstra noAtual = pq.poll();
            int u = noAtual.indicePonto;

            if (visitados.contains(u)) {
                continue;
            }
            visitados.add(u);

            if (u == indiceDestino) {
                return reconstruirCaminho(pais, indiceOrigem, indiceDestino);
            }

            for (int v : listaSucessores.get(u)) {
                // Pegar o peso da aresta entre u e v
                int pesoAresta = matrizAdjacencia[u][v];

                if (distancias.get(u) != Integer.MAX_VALUE && distancias.get(u) + pesoAresta < distancias.get(v)) {
                    distancias.put(v, distancias.get(u) + pesoAresta);
                    pais.put(v, u);
                    pq.add(new NoDijkstra(v, distancias.get(v)));
                }
            }
        }

        return null; // Caminho não encontrado
    }

    // Classe auxiliar para o Dijkstra
    private static class NoDijkstra {
        int indicePonto;
        int distancia;

        public NoDijkstra(int indicePonto, int distancia) {
            this.indicePonto = indicePonto;
            this.distancia = distancia;
        }
    }

    // Método auxiliar para reconstruir o caminho a partir do mapa de pais
    private List<PontoColeta> reconstruirCaminho(HashMap<Integer, Integer> pais, int indiceOrigem, int indiceDestino) {
        LinkedList<PontoColeta> caminho = new LinkedList<>();
        Integer atual = indiceDestino;
        while (atual != null) {
            caminho.addFirst(pontos.get(atual));
            atual = pais.get(atual);
            if (atual != null && atual == indiceOrigem && pais.get(indiceOrigem) != null) {
                // Previne loop infinito se a origem não tiver pai (como esperado)
                break;
            }
        }
        if (caminho.getFirst().getId() == pontos.get(indiceOrigem).getId()) {
            return caminho;
        }
        return null; // Caminho não pode ser reconstruído
    }

    // --- Funcionalidade Extra 1: Gerar Relatório de Graus e Caminhos ---
    public void gerarRelatorioGrafo(String nomeArquivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(nomeArquivo))) {
            writer.println("--- Relatório da Rede de Ecopontos ---");
            writer.println("Data de Geração: " + new Date());
            writer.println("\nNúmero total de Ecopontos: " + numVertices);
            writer.println("Número total de Conexões: " + listaArestas.size());

            writer.println("\n--- Detalhes dos Ecopontos ---");
            for (int i = 0; i < numVertices; i++) {
                PontoColeta ponto = pontos.get(i);
                writer.println("  " + ponto.getNomeLocal() + " (ID: " + ponto.getId() + ")");
            }

            writer.println("\n--- Grau de Cada Ecoponto ---");
            for (int i = 0; i < numVertices; i++) {
                PontoColeta ponto = pontos.get(i);
                int grau = listaSucessores.get(i).size();
                writer.println("  " + ponto.getNomeLocal() + " (ID: " + ponto.getId() + ") - Grau: " + grau);
            }

            writer.println("\n--- Conexões (Arestas) ---");
            for (Aresta aresta : listaArestas) {
                PontoColeta origem = getPontoColetaPorId(aresta.getOrigem());
                PontoColeta destino = getPontoColetaPorId(aresta.getDestino());
                writer.println("  " + origem.getNomeLocal() + " (" + origem.getId() + ") -- " + aresta.getPeso() + " --> " + destino.getNomeLocal() + " (" + destino.getId() + ")");
            }

            writer.println("\n--- Exemplo de Caminho Mais Curto (Dijkstra) ---");
            if (numVertices >= 2) {
                // Pega os dois primeiros pontos para um exemplo de caminho
                PontoColeta p1 = pontos.get(0);
                PontoColeta p2 = pontos.get(1);
                writer.println("  Caminho de " + p1.getNomeLocal() + " para " + p2.getNomeLocal() + ":");
                List<PontoColeta> caminho = dijkstraCaminhoMaisCurto(p1.getId(), p2.getId());
                if (caminho != null) {
                    int custoTotal = 0;
                    for (int i = 0; i < caminho.size(); i++) {
                        writer.print(caminho.get(i).getNomeLocal() + " (" + caminho.get(i).getId() + ")");
                        if (i < caminho.size() - 1) {
                            PontoColeta pOrigem = caminho.get(i);
                            PontoColeta pDestino = caminho.get(i + 1);
                            Integer idxOrigem = idParaIndice.get(pOrigem.getId());
                            Integer idxDestino = idParaIndice.get(pDestino.getId());
                            if (idxOrigem != null && idxDestino != null) {
                                int peso = matrizAdjacencia[idxOrigem][idxDestino];
                                custoTotal += peso;
                                writer.print(" --[" + peso + "]--> ");
                            }
                        }
                    }
                    writer.println("\n  Custo total do caminho: " + custoTotal);
                } else {
                    writer.println("  Nenhum caminho encontrado.");
                }
            } else {
                writer.println("  Não há pontos suficientes para demonstrar um caminho.");
            }

            System.out.println("\nRelatório gerado com sucesso em '" + nomeArquivo + "'");

        } catch (IOException e) {
            System.err.println("Erro ao gerar relatório: " + e.getMessage());
        }
    }
}
