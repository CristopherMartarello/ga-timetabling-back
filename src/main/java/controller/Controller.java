package controller;

/**
 *
 * @author Nathan
 */

import algoritmoGenetico.ag.AgApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class Controller {
    @Autowired
    private AgApplication algoritmo;

    /*@PostMapping("/configure")
    public ResponseEntity<?> configureAlgorithm(@RequestBody GeneticConfigDTO config) {
        algoritmo.configure(config);
        return ResponseEntity.ok("Configuração salva com sucesso");
    }
    
    @PostMapping("/generate-schedule")
    public ResponseEntity<ScheduleResultDTO> generateSchedule() {
        ScheduleResultDTO result = geneticAlgorithmService.runAlgorithm();
        return ResponseEntity.ok(result);
    }*/
}

