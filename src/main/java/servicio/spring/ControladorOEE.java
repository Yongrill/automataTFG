package servicio.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import produccion.repositorio.RepositorioOrdenProduccionViewAdHoc;
import repositorio.spring.OEEBonoResumenRepository;
import scada.modelo.OEEBonoResumen;
import servicio.spring.dto.ReporteOEEDTO;

@RestController
@RequestMapping("/api/oee")
public class ControladorOEE {

    private final IServicioOEE servicioOEE;
    private final RepositorioOrdenProduccionViewAdHoc repoOrden;
    private final OEEBonoResumenRepository oeeRepository;
    private final boolean portableProfile;

    public ControladorOEE(IServicioOEE servicioOEE,
                          RepositorioOrdenProduccionViewAdHoc repoOrden,
                          OEEBonoResumenRepository oeeRepository,
                          Environment environment) {
        this.servicioOEE = servicioOEE;
        this.repoOrden = repoOrden;
        this.oeeRepository = oeeRepository;
        this.portableProfile = Arrays.asList(environment.getActiveProfiles()).contains("portable");
    }

    @GetMapping("/resumen")
    public List<OEEBonoResumen> getAllOeeResumen() {
        return oeeRepository.findAll();
    }

    @GetMapping("/bono/{idDocBono}")
    public ResponseEntity<ReporteOEEDTO> getOeePorBono(@PathVariable String idDocBono) {
        if (portableProfile) {
            OEEBonoResumen mock = oeeRepository.findByIdDocBono(idDocBono);
            return mock != null ? ResponseEntity.ok(convertirResumenPortable(mock)) : ResponseEntity.notFound().build();
        }

        ReporteOEEDTO r = servicioOEE.calcularOEEPorBono(idDocBono);
        
        // Si no tiene historial cruzado, intentamos buscarlo en la tabla de simulación
        if (r.getIdMaquina() == null && r.getMinutosTotalesAutomata() == 0) {
            OEEBonoResumen mock = oeeRepository.findByIdDocBono(idDocBono);
            if (mock != null) {
                r.setIdDocBono(mock.getIdDocBono());
                r.setIdMaquina(mock.getNumMaquina());
                r.setNumLote("SIMULADO");
                r.setIdArticulo("ART-SIMULADO");
                r.setDisponibilidad(mock.getDisponibilidad());
                r.setRendimiento(mock.getRendimiento());
                r.setCalidad(mock.getCalidad());
                r.setOeeTotal(mock.getOeeTotal());
                r.setInyectadasReales(mock.getPiezasProducidas());
                r.setInyectadasTeoricas((int) (mock.getPiezasProducidas() * 1.05));
                r.setMinutosEnAutomatico(mock.getMinutosAutomatico());
                r.setMinutosTotalesAutomata(mock.getMinutosAutomatico() + mock.getMinutosManual() + mock.getMinutosParo());
                return ResponseEntity.ok(r);
            }
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(r);
    }
    
    @GetMapping("/orden/{idOrden}")
    public List<ReporteOEEDTO> getOeePorOrden(@PathVariable Long idOrden) {
        if (portableProfile) {
            String idDocBono = "BONO-" + idOrden;
            OEEBonoResumen mock = oeeRepository.findByIdDocBono(idDocBono);
            if (mock == null) {
                return new ArrayList<>();
            }
            List<ReporteOEEDTO> result = new ArrayList<>();
            result.add(convertirResumenPortable(mock));
            return result;
        }

        List<ReporteOEEDTO> result = new ArrayList<>();
        try {
            List<String> idsBonos = repoOrden.obtenerIdsBonosPorOrdenNativo(idOrden);
            if (idsBonos == null || idsBonos.isEmpty()) return result;
            
            for (String idDocBono : idsBonos) {
                if (idDocBono == null) continue;
                ReporteOEEDTO r = servicioOEE.calcularOEEPorBono(idDocBono);
                if (r.getMinutosTotalesAutomata() > 0) {
                    result.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/maquina/{idMaquina}")
    public List<ReporteOEEDTO> getOeePorMaquina(@PathVariable String idMaquina) {
        if (portableProfile) {
            return oeeRepository.findByNumMaquina(idMaquina).stream()
                    .map(this::convertirResumenPortable)
                    .collect(Collectors.toList());
        }

        List<ReporteOEEDTO> result = new ArrayList<>();
        try {
            List<String> idsBonos = repoOrden.obtenerIdsBonosPorMaquinaNativo(idMaquina);
            if (idsBonos == null || idsBonos.isEmpty()) return result;
            
            for (String idDocBono : idsBonos) {
                if (idDocBono == null) continue;
                ReporteOEEDTO r = servicioOEE.calcularOEEPorBono(idDocBono);
                if (r.getMinutosTotalesAutomata() > 0) {
                    result.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private ReporteOEEDTO convertirResumenPortable(OEEBonoResumen mock) {
        ReporteOEEDTO r = new ReporteOEEDTO();
        r.setIdDocBono(mock.getIdDocBono());
        r.setIdMaquina(mock.getNumMaquina());
        r.setNumLote("SIMULADO");
        r.setIdArticulo("ART-SIMULADO");
        r.setDisponibilidad(mock.getDisponibilidad());
        r.setRendimiento(mock.getRendimiento());
        r.setCalidad(mock.getCalidad());
        r.setOeeTotal(mock.getOeeTotal());
        r.setInyectadasReales(mock.getPiezasProducidas());
        r.setInyectadasTeoricas((int) (mock.getPiezasProducidas() * 1.05));
        r.setMinutosEnAutomatico(mock.getMinutosAutomatico());
        r.setMinutosTotalesAutomata(mock.getMinutosAutomatico() + mock.getMinutosManual() + mock.getMinutosParo());
        r.setTotalPiezasBuenas(mock.getPiezasBuenas());
        r.setTotalPiezasProducidas(mock.getPiezasProducidas());
        return r;
    }
}
