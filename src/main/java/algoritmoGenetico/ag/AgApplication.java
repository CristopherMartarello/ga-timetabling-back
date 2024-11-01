package algoritmoGenetico.ag;

import java.util.List;
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
        
        String caminhoArquivoEM = "src/main/resources/planilhas/Curso_EngenhariaMecanica_Matutino.xlsx";
        List<Disciplina> disciplinaEM = FileReaderService.lerHorarios(caminhoArquivoEM);
        
        String caminhoArquivoEQ = "src/main/resources/planilhas/Curso_EngenhariaQuimica_Matutino.xlsx";
        List<Disciplina> disciplinaEQ = FileReaderService.lerHorarios(caminhoArquivoEQ);
        
        String caminhoArquivoTA = "src/main/resources/planilhas/Curso_TecnicoAdministracaoVespertino_aula3a6a.xlsx";
        List<Disciplina> disciplinaTA = FileReaderService.lerHorarios(caminhoArquivoTA);
        
        String caminhoArquivoTI = "src/main/resources/planilhas/Curso_TecnicoInformaticaParaInternet_Vespertino_aula3a6a.xlsx";
        List<Disciplina> disciplinaTI = FileReaderService.lerHorarios(caminhoArquivoTI);
        
        String caminhoArquivoTM = "src/main/resources/planilhas/Curso_TecnicoMecatronicaVespertino_aula3a6a.xlsx";
        List<Disciplina> disciplinaTM = FileReaderService.lerHorarios(caminhoArquivoTM);
        
        System.out.println(disciplinaCC.get(0).getProfessor());
        System.out.println(disciplinaEM.get(0).getProfessor());
        System.out.println(disciplinaEQ.get(0).getProfessor());
        System.out.println(disciplinaTA.get(0).getProfessor());
        System.out.println(disciplinaTI.get(0).getProfessor());
        System.out.println(disciplinaTM.get(0).getProfessor());
    }

}
