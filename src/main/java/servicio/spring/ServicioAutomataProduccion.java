package servicio.spring;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dominio.modelo.ConfiguracionMaquinaPLC;
import dominio.modelo.EstadoMaquina;
import dominio.modelo.Maquina;
import plc.ClienteFinsOmron;
import produccion.modelo.OrdenProduccionView;
import produccion.servicio.IServicioOrdenProduccionView;
import repositorio.spring.EstadoActualRepository;
import repositorio.spring.HistoricoProduccionRepository;
import scada.modelo.EstadoActual;
import scada.modelo.HistoricoProduccion;
import servicio.ConfiguracionFinsPlcServicio;

@Service
@Profile("!portable")
public class ServicioAutomataProduccion implements IServicioAutomataProduccion {

    private static final Logger logger = LoggerFactory.getLogger(ServicioAutomataProduccion.class);

    private final ClienteFinsOmron clienteFins;
    private final ConfiguracionFinsPlcServicio configuracionServicio;
    private final EstadoActualRepository estadoActualRepository;
    private final HistoricoProduccionRepository historicoRepository;
    private final IServicioOrdenProduccionView ordenProduccionService;
    private final Clock clock;
    private final Map<String, CacheMaquina> cacheEstado = new HashMap<>();

    @Autowired
    public ServicioAutomataProduccion(ClienteFinsOmron clienteFins,
                                        ConfiguracionFinsPlcServicio configuracionServicio,
                                        EstadoActualRepository estadoActualRepository,
                                        HistoricoProduccionRepository historicoRepository,
                                        Clock clock) {
        this.clienteFins = clienteFins;
        this.configuracionServicio = configuracionServicio;
        this.estadoActualRepository = estadoActualRepository;
        this.historicoRepository = historicoRepository;
        this.clock = clock;
        this.ordenProduccionService = servicio.FactoriaServicios.getServicio(IServicioOrdenProduccionView.class);
    }

    @Scheduled(fixedRate = 500)
    public void escanearPlc() {
        try {
            List<ConfiguracionMaquinaPLC> config = configuracionServicio.obtenerConfiguracionesMaquinaCio();
            byte[] respuestaFins = clienteFins.leerDatosCioCrudos();
            List<Maquina> lecturaActual = clienteFins.decodificarCio(respuestaFins, config);
            
            List<OrdenProduccionView> ordenesActivas = ordenProduccionService.obtenerVista();
            Map<String, String> bonosPorMaquina = indexarBonoPorMaquina(ordenesActivas);
            
            LocalDateTime ahora = LocalDateTime.now(clock);

            for (Maquina maqActual : lecturaActual) {
                String idBono = bonosPorMaquina.get(maqActual.getId());
                procesarFlancos(maqActual, idBono, ahora);
            }
        } catch (Exception e) {
            logger.error("Fallo al escanear PLC", e);
        }
    }

    private Map<String, String> indexarBonoPorMaquina(List<OrdenProduccionView> ordenes) {
        Map<String, String> map = new HashMap<>();
        if (ordenes != null) {
            for (OrdenProduccionView orden : ordenes) {
                if (orden.getPersEnCurso() > 0 && orden.getMatricula() != null) {
                    map.put(orden.getMatricula(), orden.getIdDocBono());
                }
            }
        }
        return map;
    }

    private void procesarFlancos(Maquina actual, String idBonoActual, LocalDateTime ahora) {
        String idMaquina = actual.getId();
        CacheMaquina anterior = cacheEstado.get(idMaquina);

        if (anterior == null) {
            anterior = new CacheMaquina(actual.getEstado(), actual.getContadorPiezas(), ahora, 0, 0.0);
            cacheEstado.put(idMaquina, anterior);
            actualizarEstadoActualBd(actual, anterior.inyectadasAcumuladas, anterior.tiempoCicloCalculado, idBonoActual, anterior.fechaUltimoCambioEstado, ahora);
            insertarHistoricoBd(actual, anterior.inyectadasAcumuladas, idBonoActual, ahora, anterior.tiemposCicloIntervalo, 0.0);
            anterior.tiemposCicloIntervalo.clear();
            return;
        }

        if (anterior.estado != actual.getEstado()) {
            logger.info("Cambio de estado detectado en máquina {}: {} -> {}", 
                        idMaquina, anterior.estado, actual.getEstado());
            
            LocalDateTime marcaAnterior = anterior.fechaUltimoCambioEstado;
            double milisEnEstadoYSegundos = java.time.Duration.between(marcaAnterior, ahora).toMillis() / 1000.0;

            actualizarEstadoActualBd(actual, anterior.inyectadasAcumuladas, anterior.tiempoCicloCalculado, idBonoActual, ahora, ahora);
            
            int estIdAnterior = estadoAId(anterior.estado);

            HistoricoProduccion historicoViejo = calcularHistorico(actual.getId(), estIdAnterior, anterior.inyectadasAcumuladas, idBonoActual, ahora, anterior.tiemposCicloIntervalo, milisEnEstadoYSegundos);
            historicoRepository.save(historicoViejo);

            anterior.tiemposCicloIntervalo.clear();

            anterior.estado = actual.getEstado();
            anterior.fechaUltimoCambioEstado = ahora;
        }

        boolean francoSubida = (anterior.contadorBruto == 0 && actual.getContadorPiezas() == 1);
        
        if (francoSubida) {
            if (anterior.fechaUltimoCiclo != null) {
                double tiempoCicloSegundos = Duration.between(anterior.fechaUltimoCiclo, ahora).toMillis() / 1000.0;
                anterior.tiempoCicloCalculado = tiempoCicloSegundos;
                anterior.tiemposCicloIntervalo.add(tiempoCicloSegundos);
            }
            
            anterior.inyectadasAcumuladas++;
            anterior.fechaUltimoCiclo = ahora;

            actualizarEstadoActualBd(actual, anterior.inyectadasAcumuladas, anterior.tiempoCicloCalculado, idBonoActual, anterior.fechaUltimoCambioEstado, ahora);
        }

        anterior.contadorBruto = actual.getContadorPiezas();
    }

    private void actualizarEstadoActualBd(Maquina maquina, int inyectadas, double tiempoCiclo, String idBono, LocalDateTime fechaInicioEstado, LocalDateTime ahora) {
        EstadoActual estado = new EstadoActual(
                maquina.getId(),
                estadoAId(maquina.getEstado()),
                tiempoCiclo,
                inyectadas,
                idBono,
                fechaInicioEstado,
                ahora
        );
        estadoActualRepository.save(estado);
    }

    private void insertarHistoricoBd(Maquina maquina, int inyectadas, String idBono, LocalDateTime ahora, List<Double> tiemposIntervalo, double sumMilisEstado) {
        HistoricoProduccion historico = calcularHistorico(maquina.getId(), estadoAId(maquina.getEstado()), inyectadas, idBono, ahora, tiemposIntervalo, sumMilisEstado);
        historicoRepository.save(historico);
    }

    private HistoricoProduccion calcularHistorico(String idMaquina, int estId, int inyectadas, String idBono, LocalDateTime ahora, List<Double> tiemposIntervalo, double sumMilisEstado) {
        double media = 0.0, max = 0.0, min = 0.0, sum = 0.0, sumSq = 0.0, stdDev = 0.0;
        if (!tiemposIntervalo.isEmpty()) {
            min = Double.MAX_VALUE;
            for (double t : tiemposIntervalo) {
                sum += t;
                if (t > max) max = t;
                if (t < min) min = t;
            }
            media = sum / tiemposIntervalo.size();
            for (double t : tiemposIntervalo) {
                sumSq += Math.pow(t - media, 2);
            }
            stdDev = Math.sqrt(sumSq / tiemposIntervalo.size());
        } else {
            media = sumMilisEstado;
            max = sumMilisEstado;
            min = sumMilisEstado;
        }

        return new HistoricoProduccion(
                UUID.randomUUID().toString(),
                idMaquina,
                estId,
                media,
                max,
                min,
                stdDev,
                inyectadas,
                idBono,
                ahora
        );
    }

    private static int estadoAId(EstadoMaquina estado) {
        if (estado == EstadoMaquina.AUTOMATICO) return 0;
        if (estado == EstadoMaquina.MANUAL) return 1;
        return 2;
    }

    private static class CacheMaquina {
        EstadoMaquina estado;
        int contadorBruto;
        LocalDateTime fechaUltimoCambioEstado;
        LocalDateTime fechaUltimoCiclo;
        int inyectadasAcumuladas;
        double tiempoCicloCalculado;
        final java.util.List<Double> tiemposCicloIntervalo = new java.util.ArrayList<>();

        CacheMaquina(EstadoMaquina estado, int contadorBruto, LocalDateTime fechaUltimoCambioEstado, 
                     int inyectadasAcumuladas, double tiempoCicloCalculado) {
            this.estado = estado;
            this.contadorBruto = contadorBruto;
            this.fechaUltimoCambioEstado = fechaUltimoCambioEstado;
            this.inyectadasAcumuladas = inyectadasAcumuladas;
            this.tiempoCicloCalculado = tiempoCicloCalculado;
        }
    }
}
