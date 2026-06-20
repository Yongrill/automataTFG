package servicio.spring;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import dominio.modelo.ConfiguracionMaquinaPLC;
import dominio.modelo.EstadoMaquina;
import dominio.modelo.Maquina;
import plc.ClienteFinsOmron;
import produccion.modelo.OrdenProduccionView;
import produccion.servicio.IServicioOrdenProduccionView;
import repositorio.spring.EstadoActualRepository;
import repositorio.spring.HistoricoProduccionRepository;
import scada.modelo.HistoricoProduccion;
import servicio.ConfiguracionFinsPlcServicio;

@ExtendWith(MockitoExtension.class)
class ServicioAutomataProduccionTest {

    @Mock
    private ClienteFinsOmron clienteFins;

    @Mock
    private ConfiguracionFinsPlcServicio configuracionServicio;

    @Mock
    private EstadoActualRepository estadoActualRepository;

    @Mock
    private HistoricoProduccionRepository historicoRepository;

    @Mock
    private IServicioOrdenProduccionView ordenProduccionService;

    private Clock clock;
    private Instant fixedInstant;

    private ServicioAutomataProduccion servicio;
    private byte[] rawA;
    private ConfiguracionMaquinaPLC config;

    @BeforeEach
    void setUp() throws Exception {
        fixedInstant = Instant.parse("2026-04-16T10:00:00Z");
        clock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));

        servicio = new ServicioAutomataProduccion(
                clienteFins, configuracionServicio, estadoActualRepository, historicoRepository, clock
        );
        
        ReflectionTestUtils.setField(servicio, "ordenProduccionService", ordenProduccionService);

        config = new ConfiguracionMaquinaPLC("MQ-01", 1, 1, 1, 1);
        lenient().when(configuracionServicio.obtenerConfiguracionesMaquinaCio()).thenReturn(List.of(config));

        OrdenProduccionView orden = new OrdenProduccionView();
        orden.setIdDocBono("Bono-OEE-001");
        orden.setMatricula("MQ-01");
        orden.setPersEnCurso(1);
        lenient().when(ordenProduccionService.obtenerVista()).thenReturn(List.of(orden));

        rawA = new byte[0];
        lenient().when(clienteFins.leerDatosCioCrudos()).thenReturn(rawA);
    }

    private void simularLectura(Maquina maquina, int avanzaSegundos) throws Exception {
        if (avanzaSegundos > 0) {
            fixedInstant = fixedInstant.plusSeconds(avanzaSegundos);
            clock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
            ReflectionTestUtils.setField(servicio, "clock", clock);
        }
        when(clienteFins.decodificarCio(rawA, List.of(config))).thenReturn(List.of(maquina));
        servicio.escanearPlc();
    }

    @Test
    void test1_deberiaGuardarHistoricoAlCambiarDeAutoAParo() throws Exception {
        // Máquina arranca en Automático
        Maquina m0 = new Maquina("MQ-01", EstadoMaquina.AUTOMATICO, 0, 0.0);
        simularLectura(m0, 0);
        
        clearInvocations(historicoRepository);

        // Simulamos 100 segundos bajo estado Automático sin producir
        // Cambia a PARO
        Maquina m1 = new Maquina("MQ-01", EstadoMaquina.PARO, 0, 0.0);

        simularLectura(m1, 100);

        ArgumentCaptor<HistoricoProduccion> captor = ArgumentCaptor.forClass(HistoricoProduccion.class);
        verify(historicoRepository, times(1)).save(captor.capture());

        HistoricoProduccion histGuardado = captor.getValue();
        assertThat(histGuardado.getIdDocBono()).isEqualTo("Bono-OEE-001");
        assertThat(histGuardado.getIdEstado()).isEqualTo(0);

        assertThat(histGuardado.getTiempoCicloMedio()).isCloseTo(100.0, within(0.01));
    }

    @Test
    void test2_deberiaAcumularCiclosConFrancoDeSubidaDeContador() throws Exception {
        simularLectura(new Maquina("MQ-01", EstadoMaquina.AUTOMATICO, 0, 0.0), 0);
        clearInvocations(historicoRepository);
        
        // --- 2. Ejecución / When ---
        // Pieza 1: T=10s
        simularLectura(new Maquina("MQ-01", EstadoMaquina.AUTOMATICO, 1, 0.0), 10);
        // Reseteo sensor
        simularLectura(new Maquina("MQ-01", EstadoMaquina.AUTOMATICO, 0, 0.0), 2);
        // Pieza 2: T=15s (+15s) (Total=27s, +13s desde pieza 1) O sea, ciclo = 15s. (15 + 2 = 17 interval)
        simularLectura(new Maquina("MQ-01", EstadoMaquina.AUTOMATICO, 1, 0.0), 15);
        // Reseteo sensor
        simularLectura(new Maquina("MQ-01", EstadoMaquina.AUTOMATICO, 0, 0.0), 1);
        // Pieza 3: T=10s
        simularLectura(new Maquina("MQ-01", EstadoMaquina.AUTOMATICO, 1, 0.0), 10);

        // Disparar guardado
        simularLectura(new Maquina("MQ-01", EstadoMaquina.PARO, 0, 0.0), 5);

        ArgumentCaptor<HistoricoProduccion> captor = ArgumentCaptor.forClass(HistoricoProduccion.class);
        verify(historicoRepository, times(1)).save(captor.capture());
        
        HistoricoProduccion historico = captor.getValue();
        assertThat(historico.getInyectadas()).isEqualTo(3);
        
        assertThat(historico.getTiempoCicloMedio()).isCloseTo(14.0, within(0.01));
        assertThat(historico.getTiempoCicloMax()).isCloseTo(17.0, within(0.01));
        assertThat(historico.getTiempoCicloMin()).isCloseTo(11.0, within(0.01));
    }

    @Test
    void test3_descarteDeTiempoCicloSiProvieneDeManual() throws Exception {
        simularLectura(new Maquina("MQ-01", EstadoMaquina.MANUAL, 0, 0.0), 0);
        clearInvocations(historicoRepository);
        
        simularLectura(new Maquina("MQ-01", EstadoMaquina.AUTOMATICO, 0, 0.0), 10);
    
        simularLectura(new Maquina("MQ-01", EstadoMaquina.AUTOMATICO, 1, 0.0), 5); 
        simularLectura(new Maquina("MQ-01", EstadoMaquina.AUTOMATICO, 0, 0.0), 1);
        simularLectura(new Maquina("MQ-01", EstadoMaquina.AUTOMATICO, 1, 0.0), 10);
        
        simularLectura(new Maquina("MQ-01", EstadoMaquina.PARO, 0, 0.0), 2);

        ArgumentCaptor<HistoricoProduccion> captor = ArgumentCaptor.forClass(HistoricoProduccion.class);
        // Ha pasado de Manual -> Auto -> Paro (2 guardados)
        verify(historicoRepository, times(2)).save(captor.capture());

        // Agarramos el guardado de -> Paro (que trae los ciclos)
        HistoricoProduccion histAuto = captor.getAllValues().get(1);
        
        assertThat(histAuto.getIdEstado()).isEqualTo(0); // Auto log
        // La media deberia ser exactamente el único ciclo contabilizado = 11.0
        assertThat(histAuto.getTiempoCicloMedio()).isCloseTo(11.0, within(0.01));
    }
}