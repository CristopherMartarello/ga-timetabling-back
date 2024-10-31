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
        String caminhoArquivo = "src/main/resources/planilhas/Curso_CienciaComputacao.xlsx";
        List<Disciplina> disciplina = FileReaderService.lerHorarios(caminhoArquivo);
        System.out.println(disciplina.get(0).getProfessor());
    }

}
