package controller;

/**
 *
 * @author Nathan
 */
import DTO.GeneticConfigDTO;
import algoritmoGenetico.ag.AgApplication;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RestController
@RequestMapping("/api")  // A URL base será /api
public class Controller implements WebMvcConfigurer{

    @Autowired
    private AgApplication algoritmo;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }

    @PostMapping("/configure")  // Este mapeamento é para o endpoint /api/configure
    public ResponseEntity<String> configureAlgorithm(@Valid @RequestBody GeneticConfigDTO config) {
        System.out.println("Chegou");  // Isso é apenas para verificar se os dados estão chegando
        // Processa os dados recebidos
        System.out.println("Probabilidade de Cruzamento: " + config.getProbabilidadeCruzamento());
        System.out.println("Mutação: " + config.getProbabilidadeMutacao());
        System.out.println("Qtd. Elitismo: " + config.getQtdElitismo());
        System.out.println("Iterações: " + config.getIteracoes());
        System.out.println("Iterações sem Melhoria: " + config.getIteracoesSemMelhoria());

        // Aqui você pode chamar métodos para configurar ou executar o algoritmo
        // algoritmo.configure(config);
        
        return ResponseEntity.ok("Configuração salva com sucesso!");
    }

   /* @PostMapping("/generate-schedule")
    public ResponseEntity<ScheduleResultDTO> generateSchedule(@RequestBody GeneticConfigDTO config) {
        ScheduleResultDTO result = algoritmo.initializeAndRunAlgorithm(config);
        
        // Retorna o resultado gerado pelo algoritmo genético
        return ResponseEntity.ok(result);
    }*/
}

