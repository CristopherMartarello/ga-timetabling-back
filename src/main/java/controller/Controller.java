package controller;

/**
 *
 * @author Rafaela
 */
import DTO.GeneticConfigDTO;
import DTO.ScheduleResultDTO;
import algoritmoGenetico.ag.AgApplication;
import jakarta.validation.Valid;
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
    //função para mapear o cors
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") //back espera pela rota /api
                .allowedOrigins("http://localhost:3000") //porta utilizada
                .allowedMethods("GET", "POST", "PUT", "DELETE") //metodos que podem ser recebidos
                .allowCredentials(true);
    }
    
    
    @PostMapping("/configure")
    /*metodo post que na função acima '/api/**' ira o /configure/ 
    @RequestBody, pega a requisição e vai para a classe Genetic, que receberá as variáveis requisitadas pelo front*/
    public ResponseEntity<ScheduleResultDTO> configureAlgorithm(@Valid @RequestBody GeneticConfigDTO config) {
        //classe Schedule receberá o resultado do algoritomo, aqui chamaremos a função para começar todo o processo
        ScheduleResultDTO result = AgApplication.initializeMain(config);
        //retorno da resposta para o front
        return ResponseEntity.ok(result);
    }

}
