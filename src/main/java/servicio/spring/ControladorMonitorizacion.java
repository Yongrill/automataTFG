package servicio.spring;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import repositorio.spring.EstadoActualRepository;
import repositorio.spring.HistoricoProduccionRepository;
import scada.modelo.EstadoActual;
import scada.modelo.HistoricoProduccion;
import servicio.spring.dto.MaquinaEstadoDTO;
import servicio.spring.dto.OeeStatsDTO;

@RestController
@RequestMapping("/api/scada")
public class ControladorMonitorizacion {

    private final EstadoActualRepository estadoActualRepository;
    private final HistoricoProduccionRepository historicoRepository;

    public ControladorMonitorizacion(EstadoActualRepository estadoActualRepository,
                                     HistoricoProduccionRepository historicoRepository) {
        this.estadoActualRepository = estadoActualRepository;
        this.historicoRepository = historicoRepository;
    }

    @GetMapping("/estado-actual")
    public List<MaquinaEstadoDTO> obtenerEstadoActual() {
        List<EstadoActual> estados = estadoActualRepository.findAll();
        
        LocalDateTime ahora = LocalDateTime.now();
        return estados.stream()
                .map(estado -> new MaquinaEstadoDTO(
                        estado.getId(),
                        estado.getIdEstado(),
                        estado.getInyectadas(),
                        estado.getTiempoCiclo(),
                        estado.getIdDocBono(),
                        estado.getFechaInicioEstado() != null ? Duration.between(estado.getFechaInicioEstado(), ahora).getSeconds() : 0L
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/historico/{idBono}")
    public List<OeeStatsDTO> obtenerHistoricoPorBono(@PathVariable String idBono) {
        List<HistoricoProduccion> historicos = historicoRepository.findByIdDocBono(idBono);
        
        return historicos.stream()
                .map(hist -> new OeeStatsDTO(
                        hist.getNumMaquina(),
                        hist.getIdDocBono(),
                        hist.getIdEstado(),
                        hist.getTiempoCicloMedio(),
                        hist.getTiempoCicloMax(),
                        hist.getTiempoCicloMin(),
                        hist.getTiempoCicloStdDev(),
                        hist.getInyectadas(),
                        hist.getFechaEvento() != null ? hist.getFechaEvento().toString() : ""
                ))
                .collect(Collectors.toList());
    }
}
