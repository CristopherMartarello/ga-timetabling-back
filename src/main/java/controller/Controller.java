package controller;

/**
 *
 * @author Nathan
 */
import DTO.GeneticConfigDTO;
import DTO.ScheduleResultDTO;
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
@RequestMapping("/api")
public class Controller implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true);
    }

    @PostMapping("/configure")  // Este mapeamento é para o endpoint /api/configure
    public ResponseEntity<ScheduleResultDTO> configureAlgorithm(@Valid @RequestBody GeneticConfigDTO config) {
        // Processa os dados recebidos
        System.out.println("Probabilidade de Cruzamento: " + config.getProbabilidadeCruzamento());
        System.out.println("Mutação: " + config.getProbabilidadeMutacao());
        System.out.println("Qtd. Elitismo: " + config.getQtdElitismo());
        System.out.println("Iterações: " + config.getIteracoes());
        System.out.println("Iterações sem Melhoria: " + config.getIteracoesSemMelhoria());
        System.out.println("Calculando...");
        // Aqui você pode chamar métodos para configurar ou executar o algoritmo
        ScheduleResultDTO result = AgApplication.initializeMain(config);
        System.out.println("Enviando...");

        return ResponseEntity.ok(result);
    }

}
