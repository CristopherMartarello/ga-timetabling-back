package algoritmoGenetico.ag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import model.Disciplina;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import service.FileReaderService;

@SpringBootApplication
public class AgApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgApplication.class, args);
        String caminhoArquivoCC = "src/main/resources/planilhas/Curso_CienciaComputacao.xlsx";
        List<Disciplina> disciplinaCC = FileReaderService.lerHorarios(caminhoArquivoCC);
        int[] vetorCC = generateRandomPositionsForClassCode(disciplinaCC, true, "cc");

        String caminhoArquivoEM = "src/main/resources/planilhas/Curso_EngenhariaMecanica_Matutino.xlsx";
        List<Disciplina> disciplinaEM = FileReaderService.lerHorarios(caminhoArquivoEM);
        //int[] vetorEM = generateRandomPositionsForClassCode(disciplinaEM, true, "em");

        String caminhoArquivoEQ = "src/main/resources/planilhas/Curso_EngenhariaQuimica_Matutino.xlsx";
        List<Disciplina> disciplinaEQ = FileReaderService.lerHorarios(caminhoArquivoEQ);
        //int[] vetorEQ = generateRandomPositionsForClassCode(disciplinaEQ, true, "eq");

        String caminhoArquivoTA = "src/main/resources/planilhas/Curso_TecnicoAdministracaoVespertino_aula3a6a.xlsx";
        List<Disciplina> disciplinaTA = FileReaderService.lerHorarios(caminhoArquivoTA);
        //int[] vetorTA = generateRandomPositionsForClassCode(disciplinaTA, true, "ta");

        String caminhoArquivoTI = "src/main/resources/planilhas/Curso_TecnicoInformaticaParaInternet_Vespertino_aula3a6a.xlsx";
        List<Disciplina> disciplinaTI = FileReaderService.lerHorarios(caminhoArquivoTI);
        //int[] vetorTI = generateRandomPositionsForClassCode(disciplinaTI, true, "ti");

        String caminhoArquivoTM = "src/main/resources/planilhas/Curso_TecnicoMecatronicaVespertino_aula3a6a.xlsx";
        List<Disciplina> disciplinaTM = FileReaderService.lerHorarios(caminhoArquivoTM);
        // int[] vetorTM = generateRandomPositionsForClassCode(disciplinaTM, true, "tm");

    }

    public static int[] generateRandomPositionsForClassCode(List<Disciplina> listaAtual, boolean periodoFull, String curso) {
        int cromossomos = 0;
        int[] vetorTurmaCodigo = null;

        HashSet<Integer> codigosDeAula = new HashSet<>(); //Set para contar os códigos
        Map<Integer, Integer> contarFases = new HashMap<>(); //Map para contar o numero de fases e as vezes que aparecem
        for (Disciplina d : listaAtual) {
            int fase = d.getFase();
            contarFases.put(fase, contarFases.getOrDefault(fase, 0) + 1);
            codigosDeAula.add(d.getCodigo());
        }

        if (periodoFull) {
            cromossomos = generateCromossomeSize(5, contarFases.size());
            vetorTurmaCodigo = new int[cromossomos];
            generateCromossomeMatrice(cromossomos, listaAtual, contarFases, curso, contarFases);
        } else {
            // Fazer lógica para periodos variados (ex: 3a a 6a)
        }

        return vetorTurmaCodigo;
    }

    public static int generateCromossomeSize(int dias, int fases) {
        return (dias * 2) * fases;
    }

    public static int[] randomizeCromossomesValues(int tamanhoVetor, List<Disciplina> listaAtual, Map<Integer, Integer> fases) {
        List<Integer> chaves = new ArrayList<>(fases.keySet()); //quais as fases disponíveis (2, 4, 6, 8)
        List<Integer> valores = new ArrayList<>(fases.values());//quantas matérias possuem em cada fase no curso

        int disciplinaPadding = tamanhoVetor / chaves.size();

        Random random = new Random();
        int[] vetorRandomizado = new int[tamanhoVetor];

        int contador = 0;

        for (int i = 0; i < chaves.size(); i++) {
            int valorAtual = valores.get(i);

            // Define os limites para randomização (pegar a partir de acordo com os códigos das fases)
            int limiteInferior = contador + 1;
            int limiteSuperior = contador + valorAtual;

            // Preenche com os números aleatórios daquele intervalo
            for (int j = 0; j < disciplinaPadding && (i * disciplinaPadding + j) < tamanhoVetor; j++) {
                vetorRandomizado[i * disciplinaPadding + j] = random.nextInt(limiteSuperior - limiteInferior + 1) + limiteInferior;
            }

            contador = limiteSuperior;
        }
        return vetorRandomizado;
    }

    public static int[][] generateCromossomeMatrice(int tamanhoVetor, List<Disciplina> listaAtual, Map<Integer, Integer> fases, String curso, 
            Map<Integer, Integer> contarFases) {
        int[][] matrizCromossomo = new int[10][tamanhoVetor];
        for (int i = 0; i < 10; i++) {
            matrizCromossomo[i] = randomizeCromossomesValues(tamanhoVetor, listaAtual, fases);
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(Arrays.toString(matrizCromossomo[i]));
        }
        fitnessFunction(matrizCromossomo, curso, listaAtual, contarFases);
        return matrizCromossomo;
    }

    public static int[] fitnessFunction(int[][] matrizCromossomo, String curso, List<Disciplina> listaAtual, Map<Integer, Integer> contarFases) {
        int pontuacao = 100;
        ArrayList fitness = new ArrayList();
        ArrayList codLidos = new ArrayList();
        System.out.println(contarFases);
        int cont = 0;
        for (int coluna = 0; coluna < 1; coluna++) {
            switch (curso) {
                case "cc" -> {
                    for (int linha = 0; linha < 10; linha++) {
                        int codigo = matrizCromossomo[coluna][linha];
                        int cargaHoraria = findWorkload(codigo, listaAtual);
                        boolean existe = codLidos.contains(codigo);
                        if (!existe) {
                            System.out.println("entrei com o " + codigo);
                            for (int i = 0; i < 10; i++) { //10 = PADDING
                                if (codigo == matrizCromossomo[0][i]) {
                                    cont++;
                                }
                            }
                            codLidos.add(codigo);
                            System.out.println(codLidos);
                            if ((cargaHoraria == 80 && cont != 2) || (cargaHoraria == 40 && cont != 1)) {
                                pontuacao = pontuacao - 10;
                            }

                        }

                    }
                    System.out.println(pontuacao);
                    fitness.add(pontuacao);
                }
            }
            codLidos.clear();
        }

        return null;
    }

    public static int findWorkload(int codigo, List<Disciplina> listaAtual) {
        //TODO: Verificar a carga horaria referente ao codigo
        int cargaHoraria = 0;
        for (int i = 0; i < listaAtual.size(); i++) {
            if (codigo == listaAtual.get(i).getCodigo()) {
                cargaHoraria = listaAtual.get(i).getCargaHoraria();
            }
        }
        return cargaHoraria;
    }

}
