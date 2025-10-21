package com.mycompany.ecopontos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final String ARQUIVO_ESTACOES = "estacoes.txt";
    private static final String ARQUIVO_CONEXOES = "conexoes.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GrafoEcopontos grafo = new GrafoEcopontos(100); // Capacidade máxima para 100 pontos
        EcopontoBST bst = new EcopontoBST();
        EcopontoTrie trie = new EcopontoTrie();

        // 1. Leitura dos arquivos
        lerEcopontos(grafo, bst, trie);
        lerConexoes(grafo);

        // Menu de interação
        int opcao;
        do {
            System.out.println("\n--- Menu Principal da Rede de Ecopontos ---");
            System.out.println("1. Exibir Representações do Grafo");
            System.out.println("2. Calcular Grau de Todos os Pontos");
            System.out.println("3. Buscar Caminho Mais Curto (BFS)");
            System.out.println("4. Buscar Caminho Mais Curto (Dijkstra)");
            System.out.println("5. Buscar Ecoponto por Nome (BST)");
            System.out.println("6. Buscar Ecopontos por Prefixo (Trie)");
            System.out.println("7. Gerar Relatório do Grafo (TXT)");
            System.out.println("8. Exibir Diagrama Visual do Grafo"); // Funcionalidade extra 2
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    grafo.exibirMatrizAdjacencia();
                    grafo.exibirMatrizIncidencia();
                    grafo.exibirListaArestas();
                    grafo.exibirListaSucessores();
                    break;
                case 2:
                    grafo.calcularGrauTodosPontos();
                    break;
                case 3:
                    System.out.print("Informe o ID do ponto de origem: ");
                    int idOrigemBFS = scanner.nextInt();
                    System.out.print("Informe o ID do ponto de destino: ");
                    int idDestinoBFS = scanner.nextInt();
                    List<PontoColeta> caminhoBFS = grafo.bfsCaminhoMaisCurto(idOrigemBFS, idDestinoBFS);
                    if (caminhoBFS != null) {
                        System.out.println("Caminho BFS:");
                        for (PontoColeta p : caminhoBFS) {
                            System.out.print(p.getNomeLocal() + " (" + p.getId() + ") -> ");
                        }
                        System.out.println("FIM");
                    } else {
                        System.out.println("Caminho não encontrado ou IDs inválidos.");
                    }
                    break;
                case 4:
                    System.out.print("Informe o ID do ponto de origem: ");
                    int idOrigemDijkstra = scanner.nextInt();
                    System.out.print("Informe o ID do ponto de destino: ");
                    int idDestinoDijkstra = scanner.nextInt();
                    List<PontoColeta> caminhoDijkstra = grafo.dijkstraCaminhoMaisCurto(idOrigemDijkstra, idDestinoDijkstra);
                    if (caminhoDijkstra != null) {
                        System.out.println("Caminho Dijkstra (com pesos):");
                        int custoTotal = 0;
                        for (int i = 0; i < caminhoDijkstra.size(); i++) {
                            System.out.print(caminhoDijkstra.get(i).getNomeLocal() + " (" + caminhoDijkstra.get(i).getId() + ")");
                            if (i < caminhoDijkstra.size() - 1) {
                                PontoColeta pOrigem = caminhoDijkstra.get(i);
                                PontoColeta pDestino = caminhoDijkstra.get(i + 1);
                                // Precisamos do grafo para obter o peso da aresta
                                int peso = grafo.matrizAdjacencia[grafo.idParaIndice.get(pOrigem.getId())][grafo.idParaIndice.get(pDestino.getId())];
                                custoTotal += peso;
                                System.out.print(" --[" + peso + "]--> ");
                            }
                        }
                        System.out.println("FIM");
                        System.out.println("Custo total do caminho: " + custoTotal);
                    } else {
                        System.out.println("Caminho não encontrado ou IDs inválidos.");
                    }
                    break;
                case 5:
                    System.out.print("Digite o nome completo do ecoponto para buscar (BST): ");
                    scanner.nextLine(); // Consome a nova linha
                    String nomeBuscaBST = scanner.nextLine();
                    PontoColeta encontradoBST = bst.buscarPorNome(nomeBuscaBST);
                    if (encontradoBST != null) {
                        System.out.println("Ecoponto encontrado: " + encontradoBST);
                    } else {
                        System.out.println("Ecoponto '" + nomeBuscaBST + "' não encontrado.");
                    }
                    bst.exibirEmOrdem(); // Exibe todos em ordem após a busca
                    break;
                case 6:
                    System.out.print("Digite o prefixo para buscar ecopontos (Trie): ");
                    scanner.nextLine(); // Consome a nova linha
                    String prefixoBuscaTrie = scanner.nextLine();
                    List<PontoColeta> encontradosTrie = trie.buscarPorPrefixo(prefixoBuscaTrie);
                    if (!encontradosTrie.isEmpty()) {
                        System.out.println("Ecopontos encontrados com prefixo '" + prefixoBuscaTrie + "':");
                        for (PontoColeta p : encontradosTrie) {
                            System.out.println("  " + p);
                        }
                    } else {
                        System.out.println("Nenhum ecoponto encontrado com o prefixo '" + prefixoBuscaTrie + "'.");
                    }
                    break;
                case 7:
                    System.out.print("Informe o nome do arquivo para o relatório (ex: relatorio.txt): ");
                    scanner.nextLine(); // Consome a nova linha
                    String nomeArquivoRelatorio = scanner.nextLine();
                    grafo.gerarRelatorioGrafo(nomeArquivoRelatorio);
                    break;
                case 8:
                    System.out.println("Gerando diagrama visual do grafo...");
                    System.out.println("Este é um diagrama simplificado gerado automaticamente:");
                    gerarDiagramaVisual(grafo);
                    break;
                case 0:
                    System.out.println("Saindo do programa. Até mais!");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);

        scanner.close();
    }

    private static void lerEcopontos(GrafoEcopontos grafo, EcopontoBST bst, EcopontoTrie trie) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_ESTACOES))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 2) {
                    int id = Integer.parseInt(partes[0].trim());
                    String nome = partes[1].trim();
                    PontoColeta ponto = new PontoColeta(id, nome);
                    grafo.adicionarPonto(ponto);
                    bst.inserir(ponto);
                    trie.inserir(ponto);
                }
            }
            System.out.println("Ecopontos carregados com sucesso de " + ARQUIVO_ESTACOES);
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de ecopontos: " + e.getMessage());
        }
    }

    private static void lerConexoes(GrafoEcopontos grafo) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_CONEXOES))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(";");
                if (partes.length == 3) {
                    int idOrigem = Integer.parseInt(partes[0].trim());
                    int idDestino = Integer.parseInt(partes[1].trim());
                    int peso = Integer.parseInt(partes[2].trim());
                    grafo.adicionarAresta(idOrigem, idDestino, peso);
                }
            }
            System.out.println("Conexões carregadas com sucesso de " + ARQUIVO_CONEXOES);
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de conexões: " + e.getMessage());
        }
    }

    // --- Funcionalidade Extra 2: Geração de Diagrama Visual Simplificado ---
    // Esta função simula a criação de um diagrama visual.
    // Em um projeto real, você usaria uma biblioteca gráfica como JGraphT, Graphviz (com interface Java), ou JavaFX.
    // Aqui, vamos apenas gerar uma representação textual ou uma imagem placeholder.
    private static void gerarDiagramaVisual(GrafoEcopontos grafo) {
        System.out.println("Representação gráfica textual simplificada:");
        for (PontoColeta p : grafo.pontos) {
            System.out.println("  Ponto: " + p.getNomeLocal() + " (ID: " + p.getId() + ")");
            // Para mostrar as conexões de forma mais visual
            Integer u = grafo.idParaIndice.get(p.getId());
            if (u != null) {
                for (int v : grafo.listaSucessores.get(u)) {
                    PontoColeta vizinho = grafo.pontos.get(v);
                    int peso = grafo.matrizAdjacencia[u][v];
                    System.out.println("    --> Conectado a: " + vizinho.getNomeLocal() + " (ID: " + vizinho.getId() + ") com peso: " + peso);
                }
            }
        }
        System.out.println("\n--- Diagrama Visual (Placeholder) ---");
        System.out.println("Imagine um mapa com círculos (ecopontos) e linhas (rotas de caminhão) entre eles.");
        System.out.println("Cada linha teria o 'peso' (distância/tempo) indicado.");
        System.out.println("Um exemplo de como poderia ser o diagrama:");
        // Placeholder visual (imagem)

    }

    public static class EcopontoBST {
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
}