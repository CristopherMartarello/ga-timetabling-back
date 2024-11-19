package algoritmoGenetico.ag;

import DTO.GeneticConfigDTO;
import DTO.ScheduleResultDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import model.Disciplina;
import model.ObjetoTabela;
import model.Professor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import service.FileReaderService;

@SpringBootApplication
@ComponentScan(basePackages = "controller")
public class AgApplication {

    //Variaveis recebidas pelo front
    public static int probabilidadeCruzamentoFront, probabilidadeMutacaoFront, qtdElitismoFront,
            iteracoesFront, iteracoesSemMelhoriaFront;

    //Variaveis do fitness para cada curso
    public static ArrayList fitnessCC = new ArrayList(), fitnessEM = new ArrayList(), fitnessEQ = new ArrayList(),
            fitnessTA = new ArrayList(), fitnessTI = new ArrayList(), fitnessTM = new ArrayList();

    //Variavel que vai receber os fitness dos cursos somados
    public static ArrayList bestFitness = new ArrayList();

    //Variavel que irá receber a melhor nota dos cromossomos somados
    public static int maiorValorCromossomo = 0;

    //Variaveis da matriz de cada curso
    public static int[][] matrizCC = new int[20][40], matrizEM = new int[20][40], matrizEQ = new int[20][40],
            matrizTA = new int[20][40], matrizTI = new int[20][40], matrizTM = new int[20][40];

    //Variaveis do melhor cromossomo da matriz de cada curso
    public static int[] bestChromossomeCC = new int[40], bestChromossomeEM = new int[40], bestChromossomeEQ = new int[40],
            bestChromossomeTA = new int[40], bestChromossomeTI = new int[40], bestChromossomeTM = new int[40];

    //Variaveis dos arquivos lidos da disciplina
    public static List<Disciplina> disciplinaCC, disciplinaEM, disciplinaEQ, disciplinaTA, disciplinaTI, disciplinaTM;

    //Variaveis que vai conter a quantidade de matéria em cada fase dos cursos
    public static List<Integer> intervalosCodigosDeAulaCC, intervalosCodigosDeAulaEM, intervalosCodigosDeAulaEQ,
            intervalosCodigosDeAulaTA, intervalosCodigosDeAulaTI, intervalosCodigosDeAulaTM;

    public static void main(String[] args) {
        SpringApplication.run(AgApplication.class, args);
    }

    public static void generateRandomPositionsForClassCode(List<Disciplina> listaAtual, String curso) {
        int cromossomos = 0;

        HashSet<Integer> codigosDeAula = new HashSet<>(); //Set para contar os códigos
        Map<Integer, Integer> contarFases = new HashMap<>(); //Map para contar o numero de fases e as vezes que aparecem
        for (Disciplina d : listaAtual) {
            int fase = d.getFase();
            contarFases.put(fase, contarFases.getOrDefault(fase, 0) + 1);
            codigosDeAula.add(d.getCodigo());
        }
        // chamada da função para gerar o tamanho do cromossomo para a matriz
        cromossomos = generateCromossomeSize(5, 4);
        // chamada da função para gerar matriz
        generateCromossomeMatrice(cromossomos, listaAtual, contarFases, curso);
    }

    public static int generateCromossomeSize(int dias, int fases) {
        return (dias * 2) * fases;
    }

    public static int[] randomizeCromossomesValues(int tamanhoVetor, List<Disciplina> listaAtual, Map<Integer, Integer> fases, String curso) {
        List<Integer> chaves = new ArrayList<>(fases.keySet()); //quais as fases disponíveis (2, 4, 6, 8)
        List<Integer> valores = new ArrayList<>(fases.values());//quantas matérias possuem em cada fase no curso
        int disciplinaPadding = tamanhoVetor / 4; // padding entre cada fase

        Random random = new Random();
        //vetor que irá receber o cromossomo
        int[] vetorRandomizado = new int[tamanhoVetor];

        int contador = 0, contadorPeriodo = 1;

        for (int i = 0; i < chaves.size(); i++) {
            int valorAtual = valores.get(i);

            // Define os limites para randomização (pegar a partir de acordo com os códigos das fases)
            int limiteInferior = contador + 1;
            int limiteSuperior = contador + valorAtual;

            // Preenche com os números aleatórios daquele intervalo
            for (int j = 0; j < disciplinaPadding && (i * disciplinaPadding + j) < tamanhoVetor; j++) {
                //Verifica se for Cursos Técnicos, para as aulas de segunda serem -1
                if ((curso.equals("TA") || curso.equals("TI") || curso.equals("TM"))
                        && (contadorPeriodo == 1 || contadorPeriodo == 6 || contadorPeriodo == 11 || contadorPeriodo == 16
                        || contadorPeriodo == 21 || contadorPeriodo == 26 || contadorPeriodo == 31 || contadorPeriodo == 36)) {
                    vetorRandomizado[i * disciplinaPadding + j] = -1;
                } else {
                    vetorRandomizado[i * disciplinaPadding + j] = random.nextInt(limiteSuperior - limiteInferior + 1) + limiteInferior;
                }
                contadorPeriodo++;
            }

            contador = limiteSuperior;
        }
        if (curso.equals("EQ")) {
            for (int i = 30; i < 40; i++) {
                vetorRandomizado[i] = -1;
            }
        }
        return vetorRandomizado;
    }

    public static void generateCromossomeMatrice(int tamanhoVetor, List<Disciplina> listaAtual,
            Map<Integer, Integer> fases, String curso) {
        //matriz com os cromossomos
        int[][] matrizCromossomo = new int[20][tamanhoVetor];

        //chamada de 20 vezes a função para gerar o cromossomo
        for (int i = 0; i < 20; i++) {
            matrizCromossomo[i] = randomizeCromossomesValues(tamanhoVetor, listaAtual, fases, curso);
        }

        //varivael que vai reber o valor dos fitness em relação a carga horária
        ArrayList fitnessWorkLoad = new ArrayList();
        //varivael que vai reber o valor dos fitness em relação a disponibilidade do professor
        ArrayList fitnessProfessorAvaiability = new ArrayList();
        //variavel com a quantidade de disciplinas em cada fase
        List<Integer> intervalosCodigosDeAula = new ArrayList<>(fases.values());

        //chamada do método do primeiro fitness, em relação a carga horária
        fitnessWorkLoad = fitnessWorkLoadFunction(matrizCromossomo, curso, listaAtual, intervalosCodigosDeAula);

        //Leitura da planilha das disponibilidades dos prodessores
        String caminhoArquivoDP = "src/main/resources/planilhas/DisponibilidadeProfessores.xlsx";
        List<Professor> disponibilidadeProfessores = FileReaderService.lerHorariosProfessores(caminhoArquivoDP);

        //chamada do método do segundo fitness, em relação a disponibilidade do professor        
        fitnessProfessorAvaiability = fitnessProfessorAvaiabilityFunction(matrizCromossomo, curso, listaAtual, disponibilidadeProfessores);

        //verificação de qual curso esta fazendo a verificação do fitness para que suas variaveis possuam seus devidos valores
        for (int i = 0; i < fitnessWorkLoad.size(); i++) {
            switch (curso) {
                case "CC": {
                    //fitness do curso é calculado pelo primeiro fitness com a diferença dos descontos do segundo fitness
                    fitnessCC.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizCC = matrizCromossomo;
                    intervalosCodigosDeAulaCC = intervalosCodigosDeAula;
                    break;
                }
                case "EM": {
                    fitnessEM.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizEM = matrizCromossomo;
                    intervalosCodigosDeAulaEM = intervalosCodigosDeAula;
                    break;
                }
                case "EQ": {
                    fitnessEQ.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizEQ = matrizCromossomo;
                    intervalosCodigosDeAulaEQ = intervalosCodigosDeAula;
                    break;
                }
                case "TA": {
                    fitnessTA.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizTA = matrizCromossomo;
                    intervalosCodigosDeAulaTA = intervalosCodigosDeAula;
                    break;
                }
                case "TI": {
                    fitnessTI.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizTI = matrizCromossomo;
                    intervalosCodigosDeAulaTI = intervalosCodigosDeAula;
                    break;
                }
                case "TM": {
                    fitnessTM.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizTM = matrizCromossomo;
                    intervalosCodigosDeAulaTM = intervalosCodigosDeAula;
                    break;
                }
            }
        }
    }

    public static ArrayList fitnessWorkLoadFunction(int[][] matrizCromossomo, String curso,
            List<Disciplina> listaAtual, List<Integer> intervalosCodigosDeAula
    ) {
        //Fitness: verifica se as disciplinas estão batendo a carga horária
        int pontuacao = 1000; //pontuacao do fitness atual
        ArrayList fitness = new ArrayList(); //variavel que vai receber os 20 fitness de cada curso
        ArrayList codLidos = new ArrayList(); //verifica quais codigos já são lidos
        int contadorPadding = 0, contRepeticao = 0;
        for (int coluna = 0; coluna < matrizCromossomo.length; coluna++) {
            for (int linha = 0; linha < matrizCromossomo[0].length; linha++) {
                int cont = 0;
                int codigo = matrizCromossomo[coluna][linha];
                //verifica se tem aula no dia, já que tem cursos que não possuem aulas em algumas datas
                if (codigo != -1) {
                    //chama a função para pegar a carga horária em relação ao código da vez
                    int cargaHoraria = findWorkload(codigo, listaAtual);
                    //verifica se aquele código já não foi lido
                    boolean existe = codLidos.contains(codigo);
                    contadorPadding++;
                    if (!existe) {
                        //adiciona o código no Array de lidos
                        codLidos.add(codigo);
                        //verifica quantas vezes o codigo irá se repetir no cromossomo
                        for (int i = 0; i < matrizCromossomo[0].length; i++) {
                            if (codigo == matrizCromossomo[0][i]) {
                                cont++;
                            }
                        }
                        /*verifica se a carga horária em relação ao contador de repetição está batendo 
                        caso não tenha, é descontado 10 pontos na pontuação */
                        if ((cargaHoraria == 80 && cont != 2) || (cargaHoraria == 40 && cont != 1)) {
                            pontuacao = pontuacao - 10;
                        }
                    }

                    //como a cada 10 posições no vetor possui uma fase diferente, a cada 10 iteracoes os códigos lidos são zerados
                    if (contadorPadding % 10 == 0) {
                        /*chama a função para verificar se algum número no padding da fase não apareceu, 
                        caso não tenha aparecido será descontado 10 pontos*/
                        int novaPontuacao = verifyIntervals(matrizCromossomo, codLidos, intervalosCodigosDeAula, pontuacao, contRepeticao, curso);
                        contRepeticao++;
                        pontuacao = novaPontuacao;
                        codLidos.clear();
                    }
                }
            }
            //a cada cromossomo é adicionado o seu fitness, e as variáveis são zeradas para o processo ser refeito
            fitness.add(pontuacao);
            pontuacao = 1000;
            contRepeticao = 0;
            contadorPadding = 0;
        }
        //retorna os fitness da matriz
        return fitness;
    }

    public static ArrayList fitnessProfessorAvaiabilityFunction(int[][] matrizCromossomo,String curso,
            List<Disciplina> listaAtual, List<Professor> disponibilidadeProfessores
    ) {
        //Fitness: verifica se um professor pode realmente dar aula naquele dia
        int desconto = 0;
        //array de desconto que serão subtraidas com o fitness já obtido
        ArrayList descontos = new ArrayList();
        //lista dos dias que podem ser trabalhados
        List<String> dias = new ArrayList<>(List.of("Segunda", "Terca", "Quarta", "Quinta", "Sexta"));
        int contDias = 0;
        for (int coluna = 0; coluna < matrizCromossomo.length; coluna++) {
            for (int linha = 0; linha < matrizCromossomo[0].length; linha++) {
                //pega o código em relação a coluna e linha
                int codigo = matrizCromossomo[coluna][linha];
                //verifica se tem aula naquele dia
                if (codigo != -1) {
                    //chama a função para achar o nome do professor em relação ao código
                    String nomeProfessor = findProfessorName(codigo, listaAtual);
                    //chama a função para saber quais dias o professor não pode trabalhar, o valor é recebido em um Array
                    ArrayList indisponibilidadeProfessor = findProfessorDisponibility(nomeProfessor, disponibilidadeProfessores);
                    //verifica se o professor possui indisponibilidade
                    if (!indisponibilidadeProfessor.isEmpty()) {
                        for (int i = 0; i < indisponibilidadeProfessor.size(); i++) {
                            //Verifica se o dia que foi colocado para o professor trabalhar não possui conflito com suas indisponibilidades
                            if (indisponibilidadeProfessor.get(i).toString().equals(dias.get(contDias))) {
                                //se houver conflito, desconta 10 pontos
                                desconto += 10;
                                break;
                            }
                        }
                    }
                    contDias++;
                    //caso o contador de dias chegue a 5, é zerado para o processo ser repetido
                    if (contDias == 5) {
                        contDias = 0;
                    }
                }
            }
            //adiciona o desconto calculado na array de descontos
            descontos.add(desconto);
            //zera a variavel para se calcular das outras matrizes
            desconto = 0;

        }
        //retorna os descontos
        return descontos;
    }

    public static void fitnessBetweenCourses() {
        //Fitness: verifica se um professor não está dando aula em outra turma

        //loop do tamanho das matrizes para a comparação
        for (int i = 0; i < matrizCC.length; i++) {
            for (int j = 0; j < matrizCC[0].length; j++) {
                //pega todos os códigos das aulas referente aos cursos
                int codigoCC = matrizCC[i][j];
                int codigoEM = matrizEM[i][j];
                int codigoEQ = matrizEQ[i][j];
                int codigoTA = matrizTA[i][j];
                int codigoTI = matrizTI[i][j];
                int codigoTM = matrizTM[i][j];

                //acha o professor responsável por aquela aula
                String nomeCC = findProfessorName(codigoCC, disciplinaCC);
                String nomeEM = findProfessorName(codigoEM, disciplinaEM);
                String nomeEQ = findProfessorName(codigoEQ, disciplinaEQ);
                String nomeTA = findProfessorName(codigoTA, disciplinaTA);
                String nomeTI = findProfessorName(codigoTI, disciplinaTI);
                String nomeTM = findProfessorName(codigoTM, disciplinaTM);

                //verifica se os nomes dos cursos não são iguais dos outros cursos, caso for, descontado 10 pontos
                if (nomeCC.equals(nomeEM) || nomeCC.equals(nomeEQ) || (nomeCC.equals(nomeTA) && codigoTA != -1)
                        || (nomeCC.equals(nomeTI) && codigoTI != -1) || (nomeCC.equals(nomeTM) && codigoTM != -1)) {
                    fitnessCC.set(i, (int) fitnessCC.get(i) - 10);
                }
                if (nomeEQ.equals(nomeCC) || nomeEQ.equals(nomeEM) || (nomeEQ.equals(nomeTA) && codigoTA != -1)
                        || (nomeEQ.equals(nomeTI) && codigoTI != -1) || (nomeEQ.equals(nomeTM) && codigoTM != -1)) {
                    fitnessEQ.set(i, (int) fitnessEQ.get(i) - 10);
                }
                if (nomeEM.equals(nomeCC) || nomeEM.equals(nomeEQ) || (nomeEM.equals(nomeTA) && codigoTA != -1)
                        || (nomeEM.equals(nomeTI) && codigoTI != -1) || (nomeEM.equals(nomeTM) && codigoTM != -1)) {
                    fitnessEM.set(i, (int) fitnessEM.get(i) - 10);
                }
                //verifica se os técnicos possuem aula naquele dia
                if (codigoTA != -1) {
                    if (nomeTA.equals(nomeCC) || nomeTA.equals(nomeEQ) || nomeTA.equals(nomeEM)
                            || nomeTA.equals(nomeTI) || nomeTA.equals(nomeTM)) {
                        fitnessTA.set(i, (int) fitnessTA.get(i) - 10);
                    }
                }
                if (codigoTI != -1) {
                    if (nomeTI.equals(nomeCC) || nomeTI.equals(nomeEQ) || nomeTI.equals(nomeEM)
                            || nomeTI.equals(nomeTA) || nomeTI.equals(nomeTM)) {
                        fitnessTI.set(i, (int) fitnessTI.get(i) - 10);
                    }
                }
                if (codigoTM != -1) {
                    if (nomeTM.equals(nomeCC) || nomeTM.equals(nomeEQ) || nomeTM.equals(nomeEM)
                            || nomeTM.equals(nomeTA) || nomeTM.equals(nomeTI)) {
                        fitnessTI.set(i, (int) fitnessTI.get(i) - 10);
                    }
                }
            }
        }
    }

    public static void crossingChromossomes(
            int cromossomosElitismo,
            int probabilidadeCruzamento,
            int[][] matrizCromossomo,
            ArrayList fitness,
            String curso
    ) {
        //variavel que vai receber os cromossomos
        Map<Integer, int[]> cromossomosSelecionados = new HashMap<>();

        //o fitness é ordenado para saber quais serão selecionados por elitismo
        ArrayList<Integer> fitnessOrdenado = new ArrayList<>(fitness);
        Collections.sort(fitnessOrdenado, Collections.reverseOrder());

        //posição de cada fitness
        Set<Integer> indicesSelecionados = new HashSet<>();
        int cromossomosSelecionadosCount = 0;

        for (int i = 0; cromossomosSelecionadosCount < cromossomosElitismo && i < fitnessOrdenado.size(); i++) {
            int valorFitness = fitnessOrdenado.get(i);
            // Encontra todos os índices com esse valor de fitness
            for (int k = 0; k < fitness.size(); k++) {
                if (fitness.get(k).equals(valorFitness) && !indicesSelecionados.contains(k)) {
                    // Se ainda não foi selecionado, adiciona
                    indicesSelecionados.add(k);
                    cromossomosSelecionados.put(k, matrizCromossomo[k]);
                    cromossomosSelecionadosCount++;
                    break;
                }
            }
        }

        //variavel que ira receber os fitness somados sequencialmente
        ArrayList fitnessAbsoluto = new ArrayList();
        fitnessAbsoluto = totalFitness(fitness);
        //chama o metodo da roleta
        rouletteMethod(matrizCromossomo, fitnessAbsoluto, cromossomosSelecionados, cromossomosElitismo, probabilidadeCruzamento, curso);
    }

    public static void mutatingChromossomes(int probabilidadeMutacao,int[][] matrizCromossomo,List<Integer> intervalosCodigosDeAula, String curso
    ) {
        //random para verificar a probabilidade de mutação
        Random rand = new Random();
        for (int i = 0; i < matrizCromossomo.length; i++) {
            int nMutacao = rand.nextInt(100);
            if (nMutacao < probabilidadeMutacao) {
                //se der certo a probabilidade, faz um novo random para gerar um ponto para a mutação ser feita
                int posicao = rand.nextInt(40);
                //verifica se a mutação não for -1
                if (matrizCromossomo[i][posicao] != -1) {
                    int tamanhoPadding = 10;
                    int paddingIndex = posicao / tamanhoPadding;
                    int min = (paddingIndex == 0) ? 1 : intervalosCodigosDeAula.subList(0, paddingIndex).stream().mapToInt(Integer::intValue).sum() + 1;
                    int max = min + intervalosCodigosDeAula.get(paddingIndex) - 1;                    
                    int novoValor;
                    do {
                        novoValor = rand.nextInt((max - min) + 1) + min; //gera valor no intervalo [min, max]
                    } while (matrizCromossomo[i][posicao] == novoValor);
                    //atribui o novo valor
                    matrizCromossomo[i][posicao] = novoValor;
                }

            }
        }

    }

    public static ArrayList<Integer> totalFitness(ArrayList fitness) {
        /*
        calcula o acumulado do fitness
        acumulado[0] = fitness[0]
        acumulado[1] = fitness[0] + fitness[1]
        acumulado[2] = fitness[0] + fitness[1] + fitness[2]
        ...
         */
        ArrayList<Integer> acumulado = new ArrayList<>();
        int soma = 0;

        for (Object valor : fitness) {
            soma += (Integer) valor;
            acumulado.add(soma);
        }

        return acumulado;
    }

    public static void rouletteMethod(
            int[][] matrizCromossomo,
            ArrayList<Integer> fitnessAbsoluto,
            Map<Integer, int[]> cromossomosSelecionados,
            int cromossomosElitismo,
            int probabilidadeCruzamento,
            String curso
    ) {
        // prepara a lista para armazenar a nova geração após o cruzamento
        List<int[]> novaGeracao = new ArrayList<>();

        //cria um set para armazenar os índices dos cromossomos já selecionados
        Set<Integer> cromossomosUsados = new HashSet<>();

        //adiciona os cromossomos elitistas diretamente à nova geração
        for (Map.Entry<Integer, int[]> entry : cromossomosSelecionados.entrySet()) {
            novaGeracao.add(entry.getValue());
        }

        //forma todos os casais
        while (novaGeracao.size() < matrizCromossomo.length) {
            //chama o metodo para receber o primeiro pai
            int[] pai1 = selectChromossomeByRoulette(matrizCromossomo, fitnessAbsoluto, cromossomosSelecionados, cromossomosUsados);
            //chama o metodo para receber o segundo pai
            int[] pai2 = selectChromossomeByRoulette(matrizCromossomo, fitnessAbsoluto, cromossomosSelecionados, cromossomosUsados);
            //verifica se o cromossomo selecionado não é igual, caso seja, chama novamente a função
            while (Arrays.equals(pai1, pai2)) {
                pai2 = selectChromossomeByRoulette(matrizCromossomo, fitnessAbsoluto, cromossomosSelecionados, cromossomosUsados);
            }
            //gera um random para ver se é menor que a probabilidade de cruzamento
            Random rand = new Random();
            int numeroSorteado = rand.nextInt(100) + 1;

            if (numeroSorteado <= probabilidadeCruzamento) {
                //chama a função de crossover
                int[][] filhos = crossover(pai1, pai2);

                // adiciona na nova geração
                novaGeracao.add(filhos[0]);
                novaGeracao.add(filhos[1]);
            } else {
                novaGeracao.add(pai1);
                novaGeracao.add(pai2);
            }

            //adiciona os índices dos cromossomos usados ao set
            cromossomosUsados.add(Arrays.hashCode(pai1)); //usa hashCode para garantir unicidade no set
            cromossomosUsados.add(Arrays.hashCode(pai2)); //usa hashCode para garantir unicidade no set
        }

        //verifica qual o curso para atribuir as novas gerações as matrizes
        switch (curso) {
            case "CC" -> {
                matrizCC = novaGeracao.toArray(int[][]::new);
                break;
            }
            case "EQ" -> {
                matrizEQ = novaGeracao.toArray(int[][]::new);
                break;
            }
            case "EM" -> {
                matrizEM = novaGeracao.toArray(int[][]::new);
                break;
            }
            case "TA" -> {
                matrizTA = novaGeracao.toArray(int[][]::new);
                break;
            }
            case "TI" -> {
                matrizTI = novaGeracao.toArray(int[][]::new);
                break;
            }
            case "TM" -> {
                matrizTM = novaGeracao.toArray(int[][]::new);
                break;
            }
            default ->
                throw new AssertionError();
        }
    }

    private static int[][] crossover(int[] pai1, int[] pai2) {
        Random rand = new Random();
        int pontoCrossover = rand.nextInt(pai1.length); //ponto aleatório entre 0 e o comprimento do cromossomo

        int[] filho1 = new int[pai1.length];
        int[] filho2 = new int[pai2.length];

        //o filho 1 recebe a parte do pai1 até o ponto de crossover e a parte do pai2 após o ponto de crossover
        System.arraycopy(pai1, 0, filho1, 0, pontoCrossover);
        System.arraycopy(pai2, pontoCrossover, filho1, pontoCrossover, pai2.length - pontoCrossover);

        //o filho 2 recebe a parte do pai2 até o ponto de crossover e a parte do pai1 após o ponto de crossover
        System.arraycopy(pai2, 0, filho2, 0, pontoCrossover);
        System.arraycopy(pai1, pontoCrossover, filho2, pontoCrossover, pai1.length - pontoCrossover);

        //criação da matriz filhos
        int[][] filhos = new int[2][];
        filhos[0] = filho1;
        filhos[1] = filho2;

        return filhos;
    }

    private static int[] selectChromossomeByRoulette(int[][] matrizCromossomo, ArrayList<Integer> acumulado, Map<Integer, int[]> cromossomosSelecionados, Set<Integer> cromossomosUsados) {
        //random para gerar o ponto de corte baseado no acumulado
        Random rand = new Random();
        int pontoSorteado = rand.nextInt(acumulado.get(acumulado.size() - 1));  // Sorteando um ponto na roleta

        // verifica qual cromossomo corresponde ao ponto sorteado
        for (int i = 0; i < acumulado.size(); i++) {
            //verifica se o cromossomo foi selecionado por elitismo ou já foi utilizado
            if (pontoSorteado < acumulado.get(i)) {
                if (!cromossomosSelecionados.containsKey(i) && !cromossomosUsados.contains(i)) {
                    cromossomosUsados.add(i); //adiciona o índice ao set para garantir que não se repita
                    return matrizCromossomo[i];  //retorna o cromossomo correspondente ao ponto sorteado
                } else {
                    //se o cromossomo já foi elitista ou já foi usado, tenta outro ponto na roleta
                    return selectChromossomeByRoulette(matrizCromossomo, acumulado, cromossomosSelecionados, cromossomosUsados);
                }
            }
        }
        return matrizCromossomo[matrizCromossomo.length - 1];
    }

    public static int findWorkload(int codigo, List<Disciplina> listaAtual) {
        int cargaHoraria = 0;
        //loop com o tamanho da lista do curso relacionado
        for (int i = 0; i < listaAtual.size(); i++) {
            //verifica se o codigo é igual ao da lista
            if (codigo == listaAtual.get(i).getCodigo()) {
                //se for igual pega a carga horária em relação a posição
                cargaHoraria = listaAtual.get(i).getCargaHoraria();
                break;
            }
        }
        //retorna a carga horária obtida
        return cargaHoraria;
    }

    public static String findProfessorName(int codigo, List<Disciplina> listaAtual) {
        if (codigo != -1) {
            for (int i = 0; i < listaAtual.size(); i++) {
                if (codigo == listaAtual.get(i).getCodigo()) {
                    return listaAtual.get(i).getProfessor();
                }
            }
        }

        return "";
    }

    public static String[] findClassName(int[] bestChromossome, List<Disciplina> listaAtual) {
        String[] nomeMateria = new String[bestChromossome.length];
        for (int i = 0; i < bestChromossome.length; i++) {
            for (int j = 0; j < listaAtual.size(); j++) {
                if (listaAtual.get(j).getCodigo() == bestChromossome[i]) {
                    nomeMateria[i] = listaAtual.get(j).getNome();
                }
            }
        }

        return nomeMateria;
    }

    public static String[] findProfessorNameArray(int[] bestChromossome, List<Disciplina> listaAtual) {
        String[] nomeProfessores = new String[bestChromossome.length];
        for (int i = 0; i < bestChromossome.length; i++) {
            for (int j = 0; j < listaAtual.size(); j++) {
                if (listaAtual.get(j).getCodigo() == bestChromossome[i]) {
                    nomeProfessores[i] = listaAtual.get(j).getProfessor();
                }
            }
        }

        return nomeProfessores;
    }

    public static ArrayList<String> findProfessorDisponibility(String nome, List<Professor> disponibilidade) {
        ArrayList<String> diasIndisponiveis = new ArrayList<>();
        ArrayList<Integer> diasProfessores = new ArrayList<>();

        for (int i = 0; i < disponibilidade.size(); i++) {
            // Verifica se o objeto é do tipo Professor
            if (disponibilidade.get(i) instanceof Professor) {
                Professor professor = disponibilidade.get(i);
                //verifica se o nome é igual ao da lista de disponibilidade
                if (nome.equals(professor.getNome())) {
                    diasProfessores = professor.getHorarios();
                    for (int j = 0; j < diasProfessores.size(); j++) {
                        if (diasProfessores.get(j) == 1) {
                            switch (j) {
                                case 0:
                                    diasIndisponiveis.add("Segunda");
                                    break;
                                case 1:
                                    diasIndisponiveis.add("Terca");
                                    break;
                                case 2:
                                    diasIndisponiveis.add("Quarta");
                                    break;
                                case 3:
                                    diasIndisponiveis.add("Quinta");
                                    break;
                                case 4:
                                    diasIndisponiveis.add("Sexta");
                                    break;
                                default:
                                    throw new AssertionError("Dia inválido: " + j);
                            }
                        }
                    }
                    break;
                }
            } else {
                throw new IllegalArgumentException("Elemento inválido na lista: " + disponibilidade.get(i).getClass().getName());
            }
        }
        return diasIndisponiveis;
    }

    public static int verifyIntervals(int[][] matrizCromossomo, ArrayList codLidos, List<Integer> intervalosCodigosDeAula, int pontuacao, int nRepeticao, String curso) {
        //faz a verificação de quais códigos do padding do cromossomo não foram lidos
        int inicioLido = 0;
        int finalLido = 0;
        if (nRepeticao == 0) {
            inicioLido = 1;
            finalLido = intervalosCodigosDeAula.get(nRepeticao);
        } else {
            int x = 0;
            for (int i = 0; i < nRepeticao; i++) {
                finalLido += intervalosCodigosDeAula.get(i);
                inicioLido += intervalosCodigosDeAula.get(i);
                x = i;
            }
            inicioLido += 1;
            if (!(curso.equals("EQ") && nRepeticao == 3)) {
                finalLido += intervalosCodigosDeAula.get(x + 1);
            }
        }

        Set<Integer> esperado = new HashSet<>();
        for (int i = inicioLido; i <= finalLido; i++) {
            esperado.add(i);
        }

        Set<Integer> lidos = new HashSet<>(codLidos);
        esperado.removeAll(lidos);

        //caso algum codigo não for lido é descontado 10 pontos a cada código
        if (!esperado.isEmpty()) {
            for (Integer cod : esperado) {
                pontuacao = pontuacao - 10;
            }
        } else {
        }

        return pontuacao;
    }

    public static ScheduleResultDTO initializeMain(GeneticConfigDTO config) {
        long startTime = System.currentTimeMillis();
        clearFitness();
        //Recebe o valor das váriaveis do front
        AgApplication.probabilidadeCruzamentoFront = config.getProbabilidadeCruzamento();
        AgApplication.probabilidadeMutacaoFront = config.getProbabilidadeMutacao();
        AgApplication.qtdElitismoFront = config.getQtdElitismo();
        AgApplication.iteracoesFront = config.getIteracoes();
        AgApplication.iteracoesSemMelhoriaFront = config.getIteracoesSemMelhoria();

        //chamada da função do inicio do algoritmo
        int contadorIteracoes = initializeAlgorithm();

        //calculo do tmepo de duração do algoritmo
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        //criação do objeto da classe que irá para o back
        ScheduleResultDTO result = new ScheduleResultDTO();

        //criação do objeto que irá preencher as tabelas
        ObjetoTabela objCC = new ObjetoTabela(
                bestChromossomeCC, //codigos do melhor cromossomo
                //chamada da função para achar o nome da aula em relação ao codigo do cromossomo
                findClassName(bestChromossomeCC, disciplinaCC),
                //chamada da função para achar o nome do professor ministrante de cada aula
                findProfessorNameArray(bestChromossomeCC, disciplinaCC)
        );
        ObjetoTabela objEQ = new ObjetoTabela(
                bestChromossomeEQ,
                findClassName(bestChromossomeEQ, disciplinaEQ),
                findProfessorNameArray(bestChromossomeEQ, disciplinaEQ)
        );
        ObjetoTabela objEM = new ObjetoTabela(
                bestChromossomeEM,
                findClassName(bestChromossomeEM, disciplinaEM),
                findProfessorNameArray(bestChromossomeEM, disciplinaEM)
        );
        ObjetoTabela objTA = new ObjetoTabela(
                bestChromossomeTA,
                findClassName(bestChromossomeTA, disciplinaTA),
                findProfessorNameArray(bestChromossomeTA, disciplinaTA)
        );
        ObjetoTabela objTI = new ObjetoTabela(
                bestChromossomeTI,
                findClassName(bestChromossomeTI, disciplinaTI),
                findProfessorNameArray(bestChromossomeTI, disciplinaTI)
        );
        ObjetoTabela objTM = new ObjetoTabela(
                bestChromossomeTM,
                findClassName(bestChromossomeTM, disciplinaTM),
                findProfessorNameArray(bestChromossomeTM, disciplinaTM)
        );
        //setters do objeto result
        result.setBestFitnessScore(maiorValorCromossomo);//melhor fitness
        result.setObjTabela(new ObjetoTabela[]{objCC, objEQ, objEM, objTA, objTI, objTM});//obejtos da tabela
        result.setContIteracoes(contadorIteracoes);//quantas iterações foram feitas
        result.setIteracoesTotal(iteracoesFront);//quantidades de iteracoes que o front pediu
        result.setTempoExecucao(duration);//tempo de execução do programa

        //retorna o result
        return result;
    }

    public static int initializeAlgorithm() {
        //leitura do arquivo dos cursos
        loadCursesFiles();
        int contadorIteracoes = 0;
        int contadorSemMelhoria = 0;
        // Variáveis para armazenar o melhor fitness de cada curso
        int melhorFitness = Integer.MIN_VALUE;

        for (int i = 0; i < iteracoesFront; i++) {
            contadorIteracoes++;
            System.out.println("Contador: " + contadorIteracoes);
            //chamada de método para gerar a matriz para cada curso
            generateRandomPositionsForClassCode(disciplinaCC, "CC");
            generateRandomPositionsForClassCode(disciplinaEM, "EM");
            generateRandomPositionsForClassCode(disciplinaEQ, "EQ");
            generateRandomPositionsForClassCode(disciplinaTA, "TA");
            generateRandomPositionsForClassCode(disciplinaTI, "TI");
            generateRandomPositionsForClassCode(disciplinaTM, "TM");

            //ultima chamada do fitness
            fitnessBetweenCourses();

            //processo de cruzamento
            crossingChromossomes(qtdElitismoFront, probabilidadeCruzamentoFront, matrizCC, fitnessCC, "CC");
            crossingChromossomes(qtdElitismoFront, probabilidadeCruzamentoFront, matrizEQ, fitnessEQ, "EQ");
            crossingChromossomes(qtdElitismoFront, probabilidadeCruzamentoFront, matrizEM, fitnessEM, "EM");
            crossingChromossomes(qtdElitismoFront, probabilidadeCruzamentoFront, matrizTA, fitnessTA, "TA");
            crossingChromossomes(qtdElitismoFront, probabilidadeCruzamentoFront, matrizTI, fitnessTI, "TI");
            crossingChromossomes(qtdElitismoFront, probabilidadeCruzamentoFront, matrizTM, fitnessTM, "TM");

            //chamada de método da mutação
            mutatingChromossomes(probabilidadeMutacaoFront, matrizCC, intervalosCodigosDeAulaCC, "CC");
            mutatingChromossomes(probabilidadeMutacaoFront, matrizEQ, intervalosCodigosDeAulaEQ, "EQ");
            mutatingChromossomes(probabilidadeMutacaoFront, matrizEM, intervalosCodigosDeAulaEM, "EM");
            mutatingChromossomes(probabilidadeMutacaoFront, matrizTA, intervalosCodigosDeAulaTA, "TA");
            mutatingChromossomes(probabilidadeMutacaoFront, matrizTI, intervalosCodigosDeAulaTI, "TI");
            mutatingChromossomes(probabilidadeMutacaoFront, matrizTM, intervalosCodigosDeAulaTM, "TM");

            //recalculo do fitnnes após processo de crossover e mutação
            recallFitness();

            //variavel para obter resultado se houver melhoria
            boolean houveMelhoriaEmAlgumCurso = false;

            // Se teve melhoria, atualiza o melhor fitness            
            if (maiorValorCromossomo > melhorFitness) {
                melhorFitness = maiorValorCromossomo;
                houveMelhoriaEmAlgumCurso = true;
            }            
            //se nenhum curso teve melhoria, incrementa o contador de iterações sem melhoria
            if (!houveMelhoriaEmAlgumCurso) {
                contadorSemMelhoria++;
            }else{ //caso teve, zera o valor
                contadorSemMelhoria = 0;
            }
            //se o contadorSemMelhoria for maior ou igual ao numero de iteracoes sem melhoria que recebe do front o programa é parado
            if (contadorSemMelhoria >= iteracoesSemMelhoriaFront && iteracoesSemMelhoriaFront != 0) {
                break;
            }
        }
        //retorna a quantidade de iterações
        return contadorIteracoes;
    }

    public static void recallFitness() {
        //Refazer as chamadas de fitness
        ArrayList descontosCC = new ArrayList();
        ArrayList descontosEQ = new ArrayList();
        ArrayList descontosEM = new ArrayList();
        ArrayList descontosTA = new ArrayList();
        ArrayList descontosTI = new ArrayList();
        ArrayList descontosTM = new ArrayList();

        //leitura do arquivo da disponibilidade dos professores
        String caminhoArquivoDP = "src/main/resources/planilhas/DisponibilidadeProfessores.xlsx";
        List<Professor> disponibilidadeProfessores = FileReaderService.lerHorariosProfessores(caminhoArquivoDP);

        //chamada dos fitness feitos
        fitnessCC = fitnessWorkLoadFunction(matrizCC, "CC",
                disciplinaCC, intervalosCodigosDeAulaCC);
        descontosCC = fitnessProfessorAvaiabilityFunction(matrizCC, "CC",
                disciplinaCC, disponibilidadeProfessores);

        fitnessEQ = fitnessWorkLoadFunction(matrizEQ, "EQ",
                disciplinaEQ, intervalosCodigosDeAulaEQ);
        descontosEQ = fitnessProfessorAvaiabilityFunction(matrizEQ, "EQ",
                disciplinaEQ, disponibilidadeProfessores);

        fitnessEM = fitnessWorkLoadFunction(matrizEM, "EM",
                disciplinaEM, intervalosCodigosDeAulaEM);
        descontosEM = fitnessProfessorAvaiabilityFunction(matrizEM, "EM",
                disciplinaEM, disponibilidadeProfessores);

        fitnessTA = fitnessWorkLoadFunction(matrizTA, "TA",
                disciplinaTA, intervalosCodigosDeAulaTA);
        descontosTA = fitnessProfessorAvaiabilityFunction(matrizTA, "TA",
                disciplinaTA, disponibilidadeProfessores);

        fitnessTI = fitnessWorkLoadFunction(matrizTI, "TI",
                disciplinaTI, intervalosCodigosDeAulaTI);
        descontosTI = fitnessProfessorAvaiabilityFunction(matrizTI, "TI",
                disciplinaTI, disponibilidadeProfessores);

        fitnessTM = fitnessWorkLoadFunction(matrizTM, "TM",
                disciplinaTM, intervalosCodigosDeAulaTM);
        descontosTM = fitnessProfessorAvaiabilityFunction(matrizTM, "TM",
                disciplinaTM, disponibilidadeProfessores);

        //ajusta o valor do fitness de cada curso
        for (int i = 0; i < fitnessCC.size(); i++) {
            fitnessCC.set(i, (int) fitnessCC.get(i) - (int) descontosCC.get(i));
            fitnessEQ.set(i, (int) fitnessEQ.get(i) - (int) descontosEQ.get(i));
            fitnessEM.set(i, (int) fitnessEM.get(i) - (int) descontosEM.get(i));
            fitnessTA.set(i, (int) fitnessTA.get(i) - (int) descontosTA.get(i));
            fitnessTI.set(i, (int) fitnessTI.get(i) - (int) descontosTI.get(i));
            fitnessTM.set(i, (int) fitnessTM.get(i) - (int) descontosTM.get(i));
        }

        //chamada do último fitness
        fitnessBetweenCourses();

        //verificar qual o melhor cromossomo
        bestChromossomeVerification();

        //zera o valor dos fitness para futuras chamadas
        clearFitness();
    }

    public static void bestChromossomeVerification() {
        //variavel para receber qual posição está o maior fitness
        int posicaoMaiorFitness = 0;
        for (int i = 0; i < fitnessCC.size(); i++) {
            //recebe todos os fitness somados em uma determinada posição
            int fitness = (int) fitnessCC.get(i) + (int) fitnessEQ.get(i) + (int) fitnessEM.get(i)
                    + (int) fitnessTA.get(i) + (int) fitnessTI.get(i) + (int) fitnessTM.get(i);
            //adiciona no Array do bestFitness
            bestFitness.add(i, (Object) fitness);

            //verifica se a soma é maior que o valor do maior fitness do cromossomo
            if ((int) bestFitness.get(i) > maiorValorCromossomo) {
                //se for, a variável recebe o valor do bestFitness
                maiorValorCromossomo = (int) bestFitness.get(i);
                //a posição é atribuida a variavel
                posicaoMaiorFitness = i;

                //a variavel de melhores cromossomos dos cursos recebem seus devidos cromossomos
                bestChromossomeCC = matrizCC[posicaoMaiorFitness];
                bestChromossomeEM = matrizEM[posicaoMaiorFitness];
                bestChromossomeEQ = matrizEQ[posicaoMaiorFitness];
                bestChromossomeTA = matrizTA[posicaoMaiorFitness];
                bestChromossomeTI = matrizTI[posicaoMaiorFitness];
                bestChromossomeTM = matrizTM[posicaoMaiorFitness];
            }
        }

    }

    public static void loadCursesFiles() {
        String caminhoArquivoCC = "src/main/resources/planilhas/Curso_CienciaComputacao.xlsx";
        disciplinaCC = FileReaderService.lerHorarios(caminhoArquivoCC);

        String caminhoArquivoEM = "src/main/resources/planilhas/Curso_EngenhariaMecanica_Matutino.xlsx";
        disciplinaEM = FileReaderService.lerHorarios(caminhoArquivoEM);

        String caminhoArquivoEQ = "src/main/resources/planilhas/Curso_EngenhariaQuimica_Matutino.xlsx";
        disciplinaEQ = FileReaderService.lerHorarios(caminhoArquivoEQ);

        String caminhoArquivoTA = "src/main/resources/planilhas/Curso_TecnicoAdministracaoVespertino_aula3a6a.xlsx";
        disciplinaTA = FileReaderService.lerHorarios(caminhoArquivoTA);

        String caminhoArquivoTI = "src/main/resources/planilhas/Curso_TecnicoInformaticaParaInternet_Vespertino_aula3a6a.xlsx";
        disciplinaTI = FileReaderService.lerHorarios(caminhoArquivoTI);

        String caminhoArquivoTM = "src/main/resources/planilhas/Curso_TecnicoMecatronicaVespertino_aula3a6a.xlsx";
        disciplinaTM = FileReaderService.lerHorarios(caminhoArquivoTM);
    }

    public static void clearFitness() {
        fitnessCC.clear();
        fitnessEQ.clear();
        fitnessEM.clear();
        fitnessTA.clear();
        fitnessTI.clear();
        fitnessTM.clear();
    }
}
