package servicio.spring;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import produccion.modelo.MoldesVariantesParIny;
import produccion.modelo.OrdenProduccionView;
import produccion.repositorio.RepositorioMoldesVariantesParInyAdHoc;
import produccion.repositorio.RepositorioOrdenProduccionViewAdHoc;
import repositorio.spring.HistoricoProduccionRepository;
import scada.modelo.HistoricoProduccion;
import servicio.spring.dto.ReporteOEEDTO;
import servicio.spring.dto.ReporteOEEDTOTurno;

@Service
public class ServicioOEEImpl implements IServicioOEE {

    private final RepositorioOrdenProduccionViewAdHoc repoOrden;
    private final HistoricoProduccionRepository repoHistorico;
    private final RepositorioMoldesVariantesParInyAdHoc repoMoldes;

    public ServicioOEEImpl(RepositorioOrdenProduccionViewAdHoc repoOrden,
                           HistoricoProduccionRepository repoHistorico,
                           RepositorioMoldesVariantesParInyAdHoc repoMoldes) {
        this.repoOrden = repoOrden;
        this.repoHistorico = repoHistorico;
        this.repoMoldes = repoMoldes;
    }

    private String obtenerTurno(LocalDateTime fecha) {
        int hora = fecha.getHour();
        if (hora >= 6 && hora < 14) return "MAÑANA";
        if (hora >= 14 && hora < 22) return "TARDE";
        return "NOCHE";
    }

    private LocalDateTime finTurno(LocalDateTime fecha) {
        int hora = fecha.getHour();
        if (hora >= 6 && hora < 14) {
            return fecha.withHour(14).withMinute(0).withSecond(0).withNano(0);
        }
        if (hora >= 14 && hora < 22) {
            return fecha.withHour(22).withMinute(0).withSecond(0).withNano(0);
        }
        // NOCHE: from 22:00 to next day 06:00. If hour < 6 then end is today 06:00
        if (hora >= 22) {
            return fecha.plusDays(1).withHour(6).withMinute(0).withSecond(0).withNano(0);
        }
        // hora < 6
        return fecha.withHour(6).withMinute(0).withSecond(0).withNano(0);
    }

    @Override
    public ReporteOEEDTO calcularOEEPorBono(String idDocBono) {
        ReporteOEEDTO reporte = new ReporteOEEDTO();
        reporte.setIdDocBono(idDocBono);
        
        try {
            // 1. Obtener la Orden (ERP)
            OrdenProduccionView orden = repoOrden.getById(idDocBono);
            if (orden == null) {
                return reporte; // O lanzar excepción si no existe
            }
            reporte.setNumLote(orden.getNumLote());
            reporte.setIdArticulo(orden.getIdArticulo());
            reporte.setIdMaquina(orden.getMatricula());

            // 2. Obtener Histórico del Bono (SCADA)
            List<HistoricoProduccion> historico = repoHistorico.findByIdDocBono(idDocBono);
            if (historico == null || historico.isEmpty()) {
                return reporte; // Sin histórico de autómata, OEE Cero.
            }
            
            // Ordenar por fecha cronológicamente
            historico.sort(Comparator.comparing(HistoricoProduccion::getFechaEvento));

            // 3. Parámetros Teóricos (ERP: MoldesVariantesParIny_ANFRA)
            Long idMolde = orden.getMolde();
            Long idVariante = orden.getVariante();
            String idMaquina = orden.getMatricula();
            
            double inyMinTeorico = 0.0;
            // numFigTeorico -> A falta de un campo claro en la vista de la BD, asumiremos 1 para simplificar las inyecciones
            int numFigTeorico = 1; 

            List<MoldesVariantesParIny> paramsList = repoMoldes.buscarPorMaquinaMoldeYVariante(idMaquina, idMolde, idVariante);
            if (paramsList != null && !paramsList.isEmpty()) {
                MoldesVariantesParIny param = paramsList.get(0); // Primer registro coincidente
                if (param.getInyMin() != null) {
                    inyMinTeorico = param.getInyMin();
                }
            }

            // 4. Calcular Tiempos (Disponibilidad) y segmentar por turnos
            Map<String, ReporteOEEDTOTurno> turnosMap = new HashMap<>();
            turnosMap.put("MAÑANA", new ReporteOEEDTOTurno());
            turnosMap.put("TARDE", new ReporteOEEDTOTurno());
            turnosMap.put("NOCHE", new ReporteOEEDTOTurno());
            
            for (String ts : turnosMap.keySet()) {
                turnosMap.get(ts).setTurno(ts);
            }

            double minutosTotalesAutomata = 0.0;
            double minutosEnAutomatico = 0.0;

            for (int i = 0; i < historico.size() - 1; i++) {
                HistoricoProduccion actual = historico.get(i);
                HistoricoProduccion siguiente = historico.get(i + 1);

                LocalDateTime segStart = actual.getFechaEvento();
                LocalDateTime segEnd = siguiente.getFechaEvento();

                long totalSegundos = Duration.between(segStart, segEnd).getSeconds();
                double totalMinutos = totalSegundos / 60.0;
                if (totalMinutos <= 0) continue;

                int inyectadasIntervalo = siguiente.getInyectadas() - actual.getInyectadas();
                if (inyectadasIntervalo < 0) inyectadasIntervalo = 0;

                // Repartir el intervalo por sub-intervalos que no crucen fronteras de turno
                LocalDateTime cursor = segStart;
                int asignadas = 0;
                while (cursor.isBefore(segEnd)) {
                    LocalDateTime turnoFin = finTurno(cursor);
                    LocalDateTime partEnd = turnoFin.isBefore(segEnd) ? turnoFin : segEnd;
                    long partSegundos = Duration.between(cursor, partEnd).getSeconds();
                    double partMinutos = partSegundos / 60.0;

                    // proporción de inyectadas para este trozo (ajustando en la última iteración)
                    int asignadasParte;
                    if (partEnd.equals(segEnd)) {
                        asignadasParte = inyectadasIntervalo - asignadas; // restante
                    } else {
                        asignadasParte = (int) Math.round( (partMinutos / totalMinutos) * inyectadasIntervalo );
                        asignadas += asignadasParte;
                    }

                    String turno = obtenerTurno(cursor);
                    ReporteOEEDTOTurno rt = turnosMap.get(turno);

                    // Acumular minutos globales y por turno
                    minutosTotalesAutomata += partMinutos;
                    rt.setMinutosTotalesAutomata(rt.getMinutosTotalesAutomata() + partMinutos);
                    rt.setInyectadasReales(rt.getInyectadasReales() + asignadasParte);

                    // Si el estado inicial del tramo era automático, asignamos minutos en automático también
                    if (actual.getIdEstado() == 0) {
                        minutosEnAutomatico += partMinutos;
                        rt.setMinutosEnAutomatico(rt.getMinutosEnAutomatico() + partMinutos);
                    }

                    cursor = partEnd;
                }
            }

            reporte.setMinutosTotalesAutomata(minutosTotalesAutomata);
            reporte.setMinutosEnAutomatico(minutosEnAutomatico);

            double disponibilidad = 0.0;
            if (minutosTotalesAutomata > 0) {
                disponibilidad = (minutosEnAutomatico / minutosTotalesAutomata) * 100.0;
            }
            reporte.setDisponibilidad(disponibilidad);

            // 5. Calcular Rendimiento / Productividad
            double inyectadasTeoricas = minutosEnAutomatico * inyMinTeorico;
            
            // Inyectadas reales: diferencia entre primera y última lectura
            HistoricoProduccion primero = historico.get(0);
            HistoricoProduccion ultimo = historico.get(historico.size() - 1);
            int inyectadasReales = ultimo.getInyectadas() - primero.getInyectadas();
            if (inyectadasReales < 0) inyectadasReales = 0;

            reporte.setInyectadasReales(inyectadasReales);
            reporte.setInyectadasTeoricas((int) inyectadasTeoricas);

            double productividad = 0.0;
            if (inyectadasTeoricas > 0) {
                productividad = (inyectadasReales / inyectadasTeoricas) * 100.0;
            } else if (inyectadasReales > 0) {
                // Hay producción sin tiempo teórico configurado
                productividad = 100.0; 
            }
            reporte.setRendimiento(productividad);

            // 6. Calcular Calidad
            // Actualmente a falta de cruzar esto con las inyectadas amarillas y rojas de Mermas... 
            // Asignamos 100% de manera temporal, emulando `((TotalPiezas) / TotalPiezas) * 100`  
            double calidad = 100.0; 
            reporte.setCalidad(calidad);
            reporte.setTotalPiezasBuenas(inyectadasReales * numFigTeorico);
            reporte.setTotalPiezasProducidas(inyectadasReales * numFigTeorico);

            // 7. Calcular OEE Total
            double oeeTotal = (disponibilidad / 100.0) * (productividad / 100.0) * (calidad / 100.0) * 100.0;
            reporte.setOeeTotal(oeeTotal);

            // 8. Calcular OEE por cada TURNO
            for (ReporteOEEDTOTurno rt : turnosMap.values()) {
                if (rt.getMinutosTotalesAutomata() > 0 || rt.getInyectadasReales() > 0) {
                    double tDispo = (rt.getMinutosTotalesAutomata() > 0) ? (rt.getMinutosEnAutomatico() / rt.getMinutosTotalesAutomata()) * 100.0 : 0.0;
                    rt.setDisponibilidad(tDispo);

                    double tInyectadasTeoricas = rt.getMinutosEnAutomatico() * inyMinTeorico;
                    rt.setInyectadasTeoricas((int) tInyectadasTeoricas);

                    double tProd = 0.0;
                    if (tInyectadasTeoricas > 0) {
                        tProd = (rt.getInyectadasReales() / tInyectadasTeoricas) * 100.0;
                    } else if (rt.getInyectadasReales() > 0) {
                        tProd = 100.0;
                    }
                    rt.setRendimiento(tProd);

                    double tCalidad = 100.0;
                    rt.setCalidad(tCalidad);
                    rt.setTotalPiezasBuenas(rt.getInyectadasReales() * numFigTeorico);
                    rt.setTotalPiezasProducidas(rt.getInyectadasReales() * numFigTeorico);

                    double tOeeTotal = (tDispo / 100.0) * (tProd / 100.0) * (tCalidad / 100.0) * 100.0;
                    rt.setOeeTotal(tOeeTotal);

                    reporte.getTurnos().add(rt);
                }
            }

            return reporte;

        } catch (Exception e) {
            e.printStackTrace();
            return reporte;
        }
    }
}
