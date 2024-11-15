package algoritmoGenetico.ag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import model.Disciplina;
import model.Professor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import service.FileReaderService;

@SpringBootApplication
public class AgApplication {

    public static ArrayList fitnessCC = new ArrayList(), fitnessEM = new ArrayList(), fitnessEQ = new ArrayList(),
            fitnessTA = new ArrayList(), fitnessTI = new ArrayList(), fitnessTM = new ArrayList();

    public static int[][] matrizCC = new int[20][40], matrizEM = new int[20][40], matrizEQ = new int[20][40],
            matrizTA = new int[20][40], matrizTI = new int[20][40], matrizTM = new int[20][40];

    public static List<Disciplina> disciplinaCC, disciplinaEM, disciplinaEQ, disciplinaTA, disciplinaTI, disciplinaTM;

    public static void main(String[] args) {
        SpringApplication.run(AgApplication.class, args);

        String caminhoArquivoCC = "src/main/resources/planilhas/Curso_CienciaComputacao.xlsx";
        disciplinaCC = FileReaderService.lerHorarios(caminhoArquivoCC);
        int[] vetorCC = generateRandomPositionsForClassCode(disciplinaCC, "cc");

        String caminhoArquivoEM = "src/main/resources/planilhas/Curso_EngenhariaMecanica_Matutino.xlsx";
        disciplinaEM = FileReaderService.lerHorarios(caminhoArquivoEM);
        int[] vetorEM = generateRandomPositionsForClassCode(disciplinaEM, "em");

        String caminhoArquivoEQ = "src/main/resources/planilhas/Curso_EngenhariaQuimica_Matutino.xlsx";
        disciplinaEQ = FileReaderService.lerHorarios(caminhoArquivoEQ);
        int[] vetorEQ = generateRandomPositionsForClassCode(disciplinaEQ, "eq");

        String caminhoArquivoTA = "src/main/resources/planilhas/Curso_TecnicoAdministracaoVespertino_aula3a6a.xlsx";
        disciplinaTA = FileReaderService.lerHorarios(caminhoArquivoTA);
        int[] vetorTA = generateRandomPositionsForClassCode(disciplinaTA, "ta");

        String caminhoArquivoTI = "src/main/resources/planilhas/Curso_TecnicoInformaticaParaInternet_Vespertino_aula3a6a.xlsx";
        disciplinaTI = FileReaderService.lerHorarios(caminhoArquivoTI);
        int[] vetorTI = generateRandomPositionsForClassCode(disciplinaTI, "ti");

        String caminhoArquivoTM = "src/main/resources/planilhas/Curso_TecnicoMecatronicaVespertino_aula3a6a.xlsx";
        disciplinaTM = FileReaderService.lerHorarios(caminhoArquivoTM);
        int[] vetorTM = generateRandomPositionsForClassCode(disciplinaTM, "tm");

        fitnessBetweenCourses();
        crossingChromossomes(2, 70, matrizCC, fitnessCC);
    }

    public static int[] generateRandomPositionsForClassCode(List<Disciplina> listaAtual, String curso) {
        int cromossomos = 0;
        int[] vetorTurmaCodigo = null;

        HashSet<Integer> codigosDeAula = new HashSet<>(); //Set para contar os códigos
        Map<Integer, Integer> contarFases = new HashMap<>(); //Map para contar o numero de fases e as vezes que aparecem
        for (Disciplina d : listaAtual) {
            int fase = d.getFase();
            contarFases.put(fase, contarFases.getOrDefault(fase, 0) + 1);
            codigosDeAula.add(d.getCodigo());
        }
        cromossomos = generateCromossomeSize(5, 4);
        vetorTurmaCodigo = new int[cromossomos];
        generateCromossomeMatrice(cromossomos, listaAtual, contarFases, curso);

        return vetorTurmaCodigo;
    }

    public static int generateCromossomeSize(int dias, int fases) {
        return (dias * 2) * fases;
    }

    public static int[] randomizeCromossomesValues(int tamanhoVetor, List<Disciplina> listaAtual, Map<Integer, Integer> fases, String curso) {
        List<Integer> chaves = new ArrayList<>(fases.keySet()); //quais as fases disponíveis (2, 4, 6, 8)
        List<Integer> valores = new ArrayList<>(fases.values());//quantas matérias possuem em cada fase no curso
        int disciplinaPadding = tamanhoVetor / 4;

        Random random = new Random();
        int[] vetorRandomizado = new int[tamanhoVetor];

        int contador = 0, contadorPeriodo = 1;

        for (int i = 0; i < chaves.size(); i++) {
            int valorAtual = valores.get(i);

            // Define os limites para randomização (pegar a partir de acordo com os códigos das fases)
            int limiteInferior = contador + 1;
            int limiteSuperior = contador + valorAtual;

            // Preenche com os números aleatórios daquele intervalo
            for (int j = 0; j < disciplinaPadding && (i * disciplinaPadding + j) < tamanhoVetor; j++) {
                if ((curso.equals("ta") || curso.equals("ti") || curso.equals("tm"))
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
        return vetorRandomizado;
    }

    public static int[][] generateCromossomeMatrice(int tamanhoVetor, List<Disciplina> listaAtual, Map<Integer, Integer> fases, String curso) {
        ArrayList fitnessWorkLoad = new ArrayList();
        ArrayList fitnessProfessorAvaiability = new ArrayList();

        List<Integer> intervalosCodigosDeAula = new ArrayList<>(fases.values());
        int[][] matrizCromossomo = new int[20][tamanhoVetor];

        for (int i = 0; i < 20; i++) {
            matrizCromossomo[i] = randomizeCromossomesValues(tamanhoVetor, listaAtual, fases, curso);
        }

//        System.out.println(Arrays.toString(matrizCromossomo[0]));
        fitnessWorkLoad = fitnessWorkLoadFunction(matrizCromossomo, curso, listaAtual, intervalosCodigosDeAula);

        String caminhoArquivoDP = "src/main/resources/planilhas/DisponibilidadeProfessores.xlsx";
        List<Professor> disponibilidadeProfessores = FileReaderService.lerHorariosProfessores(caminhoArquivoDP);

        fitnessProfessorAvaiability = fitnessProfessorAvaiabilityFunction(matrizCromossomo, curso, listaAtual, disponibilidadeProfessores);
        for (int i = 0; i < fitnessWorkLoad.size(); i++) {
            switch (curso) {
                case "cc": {
                    fitnessCC.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizCC = matrizCromossomo;
                    break;
                }
                case "em": {
                    fitnessEM.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizEM = matrizCromossomo;
                    break;
                }
                case "eq": {
                    fitnessEQ.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizEQ = matrizCromossomo;
                    break;
                }
                case "ta": {
                    fitnessTA.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizTA = matrizCromossomo;
                    break;
                }
                case "ti": {
                    fitnessTI.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizTI = matrizCromossomo;
                    break;
                }
                case "tm": {
                    fitnessTM.add((int) fitnessWorkLoad.get(i) - (int) fitnessProfessorAvaiability.get(i));
                    matrizTM = matrizCromossomo;
                    break;
                }
            }
        }
        return matrizCromossomo;
    }

    public static ArrayList fitnessWorkLoadFunction(
            int[][] matrizCromossomo,
            String curso,
            List<Disciplina> listaAtual,
            List<Integer> intervalosCodigosDeAula
    ) {
        //Fitness: verifica se as disciplinas estão batendo a carga horária

        int pontuacao = 1000;
        ArrayList fitness = new ArrayList();
        ArrayList codLidos = new ArrayList();
        int contadorPadding = 0, contRepeticao = 0;
        for (int coluna = 0; coluna < matrizCromossomo.length; coluna++) {
            for (int linha = 0; linha < matrizCromossomo[0].length; linha++) {
                int cont = 0;
                int codigo = matrizCromossomo[coluna][linha];
                if (codigo != -1) { //verifica se tem aula no dia
                    int cargaHoraria = findWorkload(codigo, listaAtual);
                    boolean existe = codLidos.contains(codigo);
//                        System.out.println("----------------------------------------------");
//                        System.out.println("Codigo: " + codigo + ", esta no Vetor? " + existe);
                    contadorPadding++;
                    if (!existe) {
                        codLidos.add(codigo);
//                            System.out.println("lidos: " + codLidos);
//
//                            System.out.println("entrei com o " + codigo);
                        for (int i = 0; i < matrizCromossomo[0].length; i++) { //10 = PADDING
                            if (codigo == matrizCromossomo[0][i]) {
                                cont++;
                            }
                        }

                        if ((cargaHoraria == 80 && cont != 2) || (cargaHoraria == 40 && cont != 1)) {
                            pontuacao = pontuacao - 10;
//                                System.out.println("contador " + cont + ", carga horaria " + cargaHoraria + ", pontuacao " + pontuacao);
                        }
                    }

                    if (contadorPadding % 10 == 0) {
//                            System.out.println("APAGUEI O VETOR");
                        int novaPontuacao = verifyIntervals(matrizCromossomo, codLidos, intervalosCodigosDeAula, pontuacao, contRepeticao, curso);
                        contRepeticao++;
                        pontuacao = novaPontuacao;
                        codLidos.clear();
                    }
                }
            }
            fitness.add(pontuacao);
            pontuacao = 1000;
            contRepeticao = 0;
            contadorPadding = 0;

        }
        //System.out.println(fitness);
        return fitness;
    }

    public static ArrayList fitnessProfessorAvaiabilityFunction(
            int[][] matrizCromossomo,
            String curso,
            List<Disciplina> listaAtual,
            List<Professor> disponibilidadeProfessores
    ) {
        //Fitness: verifica se um professor pode realmente dar aula naquele dia

        int desconto = 0;
        ArrayList descontos = new ArrayList();
        List<String> dias = new ArrayList<>(List.of("Segunda", "Terca", "Quarta", "Quinta", "Sexta"));
        int contDias = 0;
        //System.out.println(Arrays.toString(matrizCromossomo[0]));
        for (int coluna = 0; coluna < matrizCromossomo.length; coluna++) {

            for (int linha = 0; linha < matrizCromossomo[0].length; linha++) {
                //System.out.println("================================");
                int codigo = matrizCromossomo[coluna][linha];
                if (codigo != -1) {
                    String nomeProfessor = findProfessorName(codigo, listaAtual);
                    //System.out.println(nomeProfessor);
                    ArrayList indisponibilidadeProfessor = findProfessorDisponibility(nomeProfessor, disponibilidadeProfessores);
                    if (indisponibilidadeProfessor.isEmpty()) {
                        //System.out.println("Professor não possui indisponibilidade");
                    }
                    for (int i = 0; i < indisponibilidadeProfessor.size(); i++) {
                        //System.out.println("Dias indisponíveis: " + indisponibilidadeProfessor.get(i).toString());
                        //System.out.println("Data pra ser marcado: " + dias.get(contDias));
                        if (indisponibilidadeProfessor.get(i).toString().equals(dias.get(contDias))) {
                            //System.out.println("DIA ENCONTRADO PORRA: " + indisponibilidadeProfessor.get(i).toString());
                            desconto += 10;
                            break;
                        }
                    }
                    contDias++;
                    if (contDias == 5) {
                        contDias = 0;
                    }
                }
            }
            descontos.add(desconto);
            desconto = 0;

        }
        //System.out.println(descontos);
        return descontos;
    }

    public static void fitnessBetweenCourses() {
        //Fitness: verifica se um professor não está dando aula em outra turma
        /*System.out.println("ANTES: " + fitnessCC);
        System.out.println("ANTES: " + fitnessEM);
        System.out.println("ANTES: " + fitnessEQ);
        System.out.println("ANTES: " + fitnessTA);
        System.out.println("ANTES: " + fitnessTI);
        System.out.println("ANTES: " + fitnessTM);*/
        //faz um 'for' do tamanho das matrizes para a comparação
        for (int i = 0; i < matrizCC.length; i++) {
            //System.out.println("--------------------");
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

                //verificações curso graduação
                if (nomeCC.equals(nomeEM)) {
                    //System.out.println(nomeCC + " x " + nomeEM);
                    //System.out.println(codigoCC + " x " + codigoEM);
                    fitnessCC.set(i, (int) fitnessCC.get(i) - 10);
                    fitnessEM.set(i, (int) fitnessEM.get(i) - 10);
                }
                if (nomeCC.equals(nomeEQ)) {
                    fitnessCC.set(i, (int) fitnessCC.get(i) - 10);
                    fitnessEQ.set(i, (int) fitnessEQ.get(i) - 10);
                }
                if (nomeEQ.equals(nomeEM)) {
                    fitnessEQ.set(i, (int) fitnessEQ.get(i) - 10);
                    fitnessEM.set(i, (int) fitnessEM.get(i) - 10);
                }
                //verificar se as aulas não forem segunda
                if (codigoTA != -1 && codigoTI != -1 && codigoTM != -1) {
                    //verificações curso técnico
                    if (nomeTA.equals(nomeTI)) {
                        fitnessTA.set(i, (int) fitnessTA.get(i) - 10);
                        fitnessTI.set(i, (int) fitnessTI.get(i) - 10);
                    }
                    if (nomeTA.equals(nomeTM)) {
                        fitnessTA.set(i, (int) fitnessTA.get(i) - 10);
                        fitnessTM.set(i, (int) fitnessTM.get(i) - 10);
                    }
                    if (nomeTI.equals(nomeTM)) {
                        fitnessTI.set(i, (int) fitnessTI.get(i) - 10);
                        fitnessTM.set(i, (int) fitnessTM.get(i) - 10);
                    }
                }

            }
        }
        /*System.out.println("DEPOIS: " + fitnessCC);
        System.out.println("DEPOIS: " + fitnessEM);
        System.out.println("DEPOIS: " + fitnessEQ);
        System.out.println("DEPOIS: " + fitnessTA);
        System.out.println("DEPOIS: " + fitnessTI);
        System.out.println("DEPOIS: " + fitnessTM);*/
    }

    public static void crossingChromossomes(
            int cromossomosElitismo,
            int probabilidadeCruzamento,
            int[][] matrizCromossomo,
            ArrayList fitness
    ) {
        Map<Integer, int[]> cromossomosSelecionados = new HashMap<>();

        ArrayList<Integer> fitnessOrdenado = new ArrayList<>(fitness);
        Collections.sort(fitnessOrdenado, Collections.reverseOrder());

        Set<Integer> indicesSelecionados = new HashSet<>();
        int cromossomosSelecionadosCount = 0;

        for (int i = 0; cromossomosSelecionadosCount < cromossomosElitismo && i < fitnessOrdenado.size(); i++) {
            int valorFitness = fitnessOrdenado.get(i);

            // Encontrar todos os índices com esse valor de fitness
            for (int k = 0; k < fitness.size(); k++) {
                if (fitness.get(k).equals(valorFitness) && !indicesSelecionados.contains(k)) {
                    // Se ainda não foi selecionado, adiciona
                    indicesSelecionados.add(k);
                    cromossomosSelecionados.put(k, matrizCromossomo[k]);
                    cromossomosSelecionadosCount++;
                    break;  // Stop, move to next elitism selection
                }
            }
        }

        ArrayList fitnessAbsoluto = new ArrayList();
        fitnessAbsoluto = totalFitness(fitness);
        rouletteMethod(matrizCromossomo, fitnessAbsoluto, cromossomosSelecionados, cromossomosElitismo, probabilidadeCruzamento);
    }

    public static void mutatingChromossomes(
            int probabilidadeMutacao,
            int[][] matrizCurso
    ) {

    }

    public static ArrayList<Integer> totalFitness(ArrayList fitness) {
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
            int probabilidadeCruzamento
    ) {
        // 1. Calculando o fitness acumulado
        ArrayList<Integer> acumulado = totalFitness(fitnessAbsoluto);

        // 2. Preparar a lista para armazenar a nova geração após o cruzamento
        List<int[]> novaGeracao = new ArrayList<>();

        // 3. Criar um set para armazenar os índices dos cromossomos já selecionados
        Set<Integer> cromossomosUsados = new HashSet<>();

        // 4. Adicionar os cromossomos elitistas diretamente à nova geração
        // Lembre-se que os cromossomos elitistas são selecionados previamente no processo
        for (Map.Entry<Integer, int[]> entry : cromossomosSelecionados.entrySet()) {
            novaGeracao.add(entry.getValue()); // Adicionando cromossomos elitistas à nova geração
        }

        // 5. Formar todos os casais (metade do tamanho da população - elitismo)
        while (novaGeracao.size() < matrizCromossomo.length) {
            
            int[] pai1 = selectChromossomeByRoulette(matrizCromossomo, acumulado, cromossomosSelecionados, cromossomosUsados);

            int[] pai2 = selectChromossomeByRoulette(matrizCromossomo, acumulado, cromossomosSelecionados, cromossomosUsados);

            while (Arrays.equals(pai1, pai2)) {
                pai2 = selectChromossomeByRoulette(matrizCromossomo, acumulado, cromossomosSelecionados, cromossomosUsados);
            }

            Random rand = new Random();
            int numeroSorteado = rand.nextInt(100) + 1;

            if (numeroSorteado <= probabilidadeCruzamento) {
                int[][] filhos = crossover(pai1, pai2);
                
                // Nova geração
                novaGeracao.add(filhos[0]); 
                novaGeracao.add(filhos[1]);
            } else {
                System.out.println("NÃO HOUVE CRUZAMENTO");
                novaGeracao.add(pai1); 
                novaGeracao.add(pai2);
            }

            // Adicionar os índices dos cromossomos usados ao set
            cromossomosUsados.add(Arrays.hashCode(pai1)); // Usa hashCode para garantir unicidade no set
            cromossomosUsados.add(Arrays.hashCode(pai2)); // Usa hashCode para garantir unicidade no set
        }

        System.out.println("Nova geração: ");
        for (int[] cromossomo : novaGeracao) {
            System.out.println(Arrays.toString(cromossomo));
        }
    }

    private static int[][] crossover(int[] pai1, int[] pai2) {
        
        Random rand = new Random();
        int pontoCrossover = rand.nextInt(pai1.length); // Ponto aleatório entre 0 e o comprimento do cromossomo
        System.out.println("CORTE " + pontoCrossover);

        int[] filho1 = new int[pai1.length];
        int[] filho2 = new int[pai2.length];

        // O filho 1 recebe a parte do pai1 até o ponto de crossover e a parte do pai2 após o ponto de crossover
        System.arraycopy(pai1, 0, filho1, 0, pontoCrossover);
        System.arraycopy(pai2, pontoCrossover, filho1, pontoCrossover, pai2.length - pontoCrossover);

        // O filho 2 recebe a parte do pai2 até o ponto de crossover e a parte do pai1 após o ponto de crossover
        System.arraycopy(pai2, 0, filho2, 0, pontoCrossover);
        System.arraycopy(pai1, pontoCrossover, filho2, pontoCrossover, pai1.length - pontoCrossover);

        int[][] filhos = new int[2][];
        filhos[0] = filho1;
        filhos[1] = filho2;
        
        System.out.println("PAI 1 " + Arrays.toString(pai1));
        System.out.println("PAI 2 " + Arrays.toString(pai2));
        System.out.println("=================================");
        System.out.println("FILHO 1 " + Arrays.toString(filho1));
        System.out.println("FILHO 2 " + Arrays.toString(filho2));

        return filhos;
    }

    private static int[] selectChromossomeByRoulette(int[][] matrizCromossomo, ArrayList<Integer> acumulado, Map<Integer, int[]> cromossomosSelecionados, Set<Integer> cromossomosUsados) {
        Random rand = new Random();
        int pontoSorteado = rand.nextInt(acumulado.get(acumulado.size() - 1));  // Sorteando um ponto na roleta

        // Verificando qual cromossomo corresponde ao ponto sorteado
        for (int i = 0; i < acumulado.size(); i++) {
            // Verificar se o cromossomo foi selecionado por elitismo ou já foi utilizado
            if (pontoSorteado < acumulado.get(i)) {
                if (!cromossomosSelecionados.containsKey(i) && !cromossomosUsados.contains(i)) {
                    cromossomosUsados.add(i); // Adiciona o índice ao set para garantir que não se repita
                    return matrizCromossomo[i];  // Retorna o cromossomo correspondente ao ponto sorteado
                } else {
                    // Se o cromossomo já foi elitista ou já foi usado, tenta outro ponto na roleta
                    return selectChromossomeByRoulette(matrizCromossomo, acumulado, cromossomosSelecionados, cromossomosUsados);
                }
            }
        }

        return matrizCromossomo[matrizCromossomo.length - 1];
    }

    public static int findWorkload(int codigo, List<Disciplina> listaAtual) {
        int cargaHoraria = 0;
        for (int i = 0; i < listaAtual.size(); i++) {
            if (codigo == listaAtual.get(i).getCodigo()) {
                cargaHoraria = listaAtual.get(i).getCargaHoraria();
            }
        }
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

    public static ArrayList findProfessorDisponibility(String nome, List<Professor> disponibilidade) {
        ArrayList diasIndisponiveis = new ArrayList();
        ArrayList diasProfessores = new ArrayList();
        for (int i = 0; i < disponibilidade.size(); i++) {
            if (nome.equals(disponibilidade.get(i).getNome())) {
                diasProfessores = disponibilidade.get(i).getHorarios();
                for (int j = 0; j < diasProfessores.size(); j++) {
                    if ((int) diasProfessores.get(j) == 1) {
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
                                throw new AssertionError();
                        }
                    }

                }
                break;
            }
        }
        return diasIndisponiveis;
    }

    public static int verifyIntervals(int[][] matrizCromossomo, ArrayList codLidos, List<Integer> intervalosCodigosDeAula, int pontuacao, int nRepeticao, String curso) {
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
            if (!(curso.equals("eq") && nRepeticao == 3)) {
                finalLido += intervalosCodigosDeAula.get(x + 1);
            }

        }

        Set<Integer> esperado = new HashSet<>();
        for (int i = inicioLido; i <= finalLido; i++) {
            esperado.add(i);
        }

        Set<Integer> lidos = new HashSet<>(codLidos);
        esperado.removeAll(lidos);

        if (!esperado.isEmpty()) {
            for (Integer cod : esperado) {
                pontuacao = pontuacao - 10;
            }
//            System.out.println("De " + inicioLido + " a " + finalLido + " faltou: " + esperado + ", pontuacao " + pontuacao);
        } else {
//            System.out.println("Não faltou");
        }

        return pontuacao;
    }

}
