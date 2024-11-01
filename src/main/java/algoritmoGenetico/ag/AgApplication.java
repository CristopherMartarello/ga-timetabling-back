package algoritmoGenetico.ag;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
        int[] vetorCC = generateRandomPositionsForClassCode(disciplinaCC, true);
        
        String caminhoArquivoEM = "src/main/resources/planilhas/Curso_EngenhariaMecanica_Matutino.xlsx";
        List<Disciplina> disciplinaEM = FileReaderService.lerHorarios(caminhoArquivoEM);
        int[] vetorEM = generateRandomPositionsForClassCode(disciplinaEM, true);

        String caminhoArquivoEQ = "src/main/resources/planilhas/Curso_EngenhariaQuimica_Matutino.xlsx";
        List<Disciplina> disciplinaEQ = FileReaderService.lerHorarios(caminhoArquivoEQ);
        int[] vetorEQ = generateRandomPositionsForClassCode(disciplinaEQ, true);
        
        String caminhoArquivoTA = "src/main/resources/planilhas/Curso_TecnicoAdministracaoVespertino_aula3a6a.xlsx";
        List<Disciplina> disciplinaTA = FileReaderService.lerHorarios(caminhoArquivoTA);
        int[] vetorTA = generateRandomPositionsForClassCode(disciplinaTA, true);
        
        String caminhoArquivoTI = "src/main/resources/planilhas/Curso_TecnicoInformaticaParaInternet_Vespertino_aula3a6a.xlsx";
        List<Disciplina> disciplinaTI = FileReaderService.lerHorarios(caminhoArquivoTI);
        int[] vetorTI = generateRandomPositionsForClassCode(disciplinaTI, true);
        
        String caminhoArquivoTM = "src/main/resources/planilhas/Curso_TecnicoMecatronicaVespertino_aula3a6a.xlsx";
        List<Disciplina> disciplinaTM = FileReaderService.lerHorarios(caminhoArquivoTM);
        int[] vetorTM = generateRandomPositionsForClassCode(disciplinaTM, true);
        
//        TO FIX: Problema ao ler outras planilhas (só está dando certo com CC)
//        System.out.println(disciplinaCC.get(0).getProfessor());
//        System.out.println(disciplinaEM.get(0).getProfessor());
//        System.out.println(disciplinaEQ.get(0).getProfessor());
//        System.out.println(disciplinaTA.get(0).getProfessor());
//        System.out.println(disciplinaTI.get(0).getProfessor());
//        System.out.println(disciplinaTM.get(0).getProfessor());
    }
    
    public static int[] generateRandomPositionsForClassCode(List<Disciplina> listaAtual, boolean periodoFull) {
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
            cromossomos = generateNumberOfCromossomes(5, contarFases.size());
            vetorTurmaCodigo = new int[cromossomos];
//            vetorTurmaCodigo = randomizeCromossomesValues(cromossomos, listaAtual, contarFases.size());
        } else {
            // Fazer lógica para periodos variados (ex: 3a a 6a)
        }
        
        return vetorTurmaCodigo;
    }
    
    public static int generateNumberOfCromossomes(int dias, int fases) {
        return (dias * 2) * fases;
    }
    
//    public static int[] randomizeCromossomesValues(int tamanhoVetor, List<Disciplina> listaAtual, int fases) {
//        
//    }

}
