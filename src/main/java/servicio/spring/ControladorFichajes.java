package servicio.spring;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import repositorio.spring.FichajeBonoRepository;
import scada.modelo.FichajeBono;

@RestController
@RequestMapping("/api/fichajes")
public class ControladorFichajes {

    private final FichajeBonoRepository repo;

    public ControladorFichajes(FichajeBonoRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/bono/{idDocBono}")
    public List<FichajeBono> getFichajesPorBono(@PathVariable String idDocBono) {
        return repo.findByIdDocBonoOrderByFechaFichajeDesc(idDocBono);
    }
}