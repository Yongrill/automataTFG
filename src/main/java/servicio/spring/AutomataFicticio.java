package servicio.spring;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import repositorio.spring.EstadoActualRepository;
import repositorio.spring.HistoricoProduccionRepository;
import repositorio.spring.OEEBonoResumenRepository;
import repositorio.spring.FichajeBonoRepository;
import scada.modelo.EstadoActual;
import scada.modelo.HistoricoProduccion;
import scada.modelo.OEEBonoResumen;
import scada.modelo.FichajeBono;

@Service
@Profile("portable")
public class AutomataFicticio {

    private static final Logger logger = LoggerFactory.getLogger(AutomataFicticio.class);
    private final EstadoActualRepository estadoActualRepository;
    private final HistoricoProduccionRepository historicoRepository;
    private final OEEBonoResumenRepository oeeRepository;
    private final FichajeBonoRepository fichajeRepository;
    private final Random random = new Random();

    // Variables de estado interno para simulacion
    private int inyectadasMaq1 = 500;
    private int inyectadasMaq2 = 200;
    private int inyectadasMaq3 = 100;
    private int inyectadasMaq4 = 50;

    @Autowired
    public AutomataFicticio(EstadoActualRepository estadoActualRepository,
                            HistoricoProduccionRepository historicoRepository,
                            OEEBonoResumenRepository oeeRepository,
                            FichajeBonoRepository fichajeRepository) {
        this.estadoActualRepository = estadoActualRepository;
        this.historicoRepository = historicoRepository;
        this.oeeRepository = oeeRepository;
        this.fichajeRepository = fichajeRepository;
    }

    @PostConstruct
    public void initFakeOEEData() {
        if (oeeRepository.count() < 5) {
            logger.info("Generando datos ficticios de OEEBonoResumen, Historico y Fichajes...");
            String[] maquinas = {"MAQ-01", "MAQ-02", "MAQ-03", "MAQ-04"};
            String[] pausas = {"mantenimiento", "rotura"};
            
            for (int i = 1; i <= 10; i++) {
                String idDocBono = "BONO-200" + i;
                String numMaquina = maquinas[random.nextInt(maquinas.length)];
                LocalDateTime fechaInicio = LocalDateTime.now().minusDays(random.nextInt(5)).minusHours(8);
                LocalDateTime fechaFin = fechaInicio.plusHours(8);
                int producidas = 1000 + random.nextInt(5000);
                int idEmpleado = 1 + random.nextInt(99);

                // 1. Resumen OEE
                OEEBonoResumen oee = new OEEBonoResumen();
                oee.setId(UUID.randomUUID().toString());
                oee.setIdDocBono(idDocBono);
                oee.setNumMaquina(numMaquina);
                oee.setFechaInicio(fechaInicio);
                oee.setFechaFin(fechaFin);
                
                oee.setPiezasProducidas(producidas);
                oee.setPiezasBuenas((int) (producidas * (0.85 + random.nextDouble() * 0.14)));
                oee.setPiezasScrap((int) (producidas * 0.05 * random.nextDouble()));
                oee.setPiezasRevision(producidas - oee.getPiezasBuenas() - oee.getPiezasScrap());
                
                oee.setTiempoCicloTeoricoSeg(10.0 + random.nextDouble() * 10);
                oee.setTiempoCicloMedioSeg(oee.getTiempoCicloTeoricoSeg() + random.nextDouble() * 2);
                oee.setTiempoCicloStdDevSeg(random.nextDouble());
                
                oee.setMinutosAutomatico(400 + random.nextDouble() * 50);
                oee.setMinutosManual(10 + random.nextDouble() * 20);
                oee.setMinutosParo(random.nextDouble() * 30);
                
                oee.setDisponibilidad(80 + random.nextDouble() * 20);
                oee.setRendimiento(75 + random.nextDouble() * 25);
                oee.setCalidad((double) oee.getPiezasBuenas() / producidas * 100);
                oee.setOeeTotal((oee.getDisponibilidad() / 100) * (oee.getRendimiento() / 100) * (oee.getCalidad() / 100) * 100);
                oee.setFechaCalculo(LocalDateTime.now());
                oeeRepository.save(oee);

                // 2. Histórico Producción Simulado (para que ServicioOEE calcule dinámicamente)
                double tc = oee.getTiempoCicloMedioSeg();
                historicoRepository.save(new HistoricoProduccion(UUID.randomUUID().toString(), numMaquina, 0, tc, tc+1, tc-1, 0.2, 0, idDocBono, fechaInicio));
                historicoRepository.save(new HistoricoProduccion(UUID.randomUUID().toString(), numMaquina, 1, tc, tc+1, tc-1, 0.2, producidas / 2, idDocBono, fechaInicio.plusHours(4)));
                historicoRepository.save(new HistoricoProduccion(UUID.randomUUID().toString(), numMaquina, 0, tc, tc+1, tc-1, 0.2, producidas / 2, idDocBono, fechaInicio.plusHours(4).plusMinutes(30)));
                historicoRepository.save(new HistoricoProduccion(UUID.randomUUID().toString(), numMaquina, 0, tc, tc+1, tc-1, 0.2, producidas, idDocBono, fechaFin));

                // 3. Fichajes de Bono
                fichajeRepository.save(new FichajeBono(UUID.randomUUID().toString(), idDocBono, idEmpleado, "verde", null, fechaInicio));
                
                if (random.nextBoolean()) {
                    fichajeRepository.save(new FichajeBono(UUID.randomUUID().toString(), idDocBono, idEmpleado, "pausa", pausas[random.nextInt(pausas.length)], fechaInicio.plusHours(4)));
                    fichajeRepository.save(new FichajeBono(UUID.randomUUID().toString(), idDocBono, idEmpleado, "fin_pausa", null, fechaInicio.plusHours(4).plusMinutes(30)));
                }

                if (random.nextDouble() > 0.7) {
                    fichajeRepository.save(new FichajeBono(UUID.randomUUID().toString(), idDocBono, idEmpleado, "amarillo", "Ajuste de parámetros", fechaInicio.plusHours(6)));
                }
                
                if (random.nextDouble() > 0.8) {
                    fichajeRepository.save(new FichajeBono(UUID.randomUUID().toString(), idDocBono, idEmpleado, "rojo", "Piezas defectuosas", fechaInicio.plusHours(7)));
                }
            }
        }
    }

    @Scheduled(fixedRate = 5000) // Simular cada 5 segundos
    public void simularProduccion() {
        logger.info("Simulando producción ficticia...");
        LocalDateTime ahora = LocalDateTime.now();

        // Simular Maquina 1 (Automático)
        inyectadasMaq1 += 1;
        simularMaquina("MAQ-01", 0, 12.0 + random.nextDouble(), inyectadasMaq1, "BONO-1001", ahora, 10);

        // Simular Maquina 2 (Manual/Paro intermitente 20%)
        int estMaq2 = random.nextDouble() > 0.8 ? 1 : 0;
        if (estMaq2 == 0) inyectadasMaq2 += 1;
        simularMaquina("MAQ-02", estMaq2, 15.0 + random.nextDouble() * 2, inyectadasMaq2, "BONO-1002", ahora, 8);

        // Simular Maquina 3 (Automático rápido)
        inyectadasMaq3 += 2;
        simularMaquina("MAQ-03", 0, 8.0 + random.nextDouble(), inyectadasMaq3, "BONO-1003", ahora, 15);

        // Simular Maquina 4 (Paro frecuente 40%)
        int estMaq4 = random.nextDouble() > 0.6 ? 1 : 0;
        if (estMaq4 == 0) inyectadasMaq4 += 1;
        simularMaquina("MAQ-04", estMaq4, 20.0 + random.nextDouble() * 3, inyectadasMaq4, "BONO-1004", ahora, 5);
    }

    private void simularMaquina(String numMaquina, int idEstado, double tiempoCiclo, int inyectadas, String idDocBono, LocalDateTime ahora, int frecHistorico) {
        EstadoActual estado = new EstadoActual(numMaquina, idEstado, tiempoCiclo, inyectadas, idDocBono, ahora.minusMinutes(10), ahora);
        estadoActualRepository.save(estado);

        if (idEstado == 0 && inyectadas % frecHistorico == 0) {
            HistoricoProduccion hist = new HistoricoProduccion(
                UUID.randomUUID().toString(), numMaquina, idEstado, tiempoCiclo, 
                tiempoCiclo + 1.0, Math.max(0, tiempoCiclo - 1.0), 0.3, inyectadas, idDocBono, ahora
            );
            historicoRepository.save(hist);
        }
    }
}
