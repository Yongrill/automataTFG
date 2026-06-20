package servicio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dominio.modelo.ConfiguracionMaquinaPLC;
import repositorio.FactoriaRepositorios;
import repositorio.Repositorio;
import repositorio.RepositorioException;
import scada.modelo.MaquinasAutomataAnfra;
import scada.modelo.MemoriaAutomataAnfra;

public class ConfiguracionFinsPlcServicio {

    private final Repositorio<MaquinasAutomataAnfra, String> repositorioMaquinas;
    private final Repositorio<MemoriaAutomataAnfra, String> repositorioMemorias;

    @SuppressWarnings("unchecked")
    public ConfiguracionFinsPlcServicio() {
        this.repositorioMaquinas = FactoriaRepositorios.getRepositorio(MaquinasAutomataAnfra.class);
        this.repositorioMemorias = FactoriaRepositorios.getRepositorio(MemoriaAutomataAnfra.class);
    }

    public List<ConfiguracionMaquinaPLC> obtenerConfiguracionesMaquinaCio() {
        try {
            List<MaquinasAutomataAnfra> maquinas = repositorioMaquinas.getAll();
            List<MemoriaAutomataAnfra> memorias = repositorioMemorias.getAll();

            Map<Integer, MemoriaAutomataAnfra> memoriaPorId = new HashMap<>();
            for (MemoriaAutomataAnfra memoria : memorias) {
                if (memoria.getIdMemoria() != null) {
                    memoriaPorId.put(memoria.getIdMemoria(), memoria);
                }
            }

            List<ConfiguracionMaquinaPLC> configuraciones = new ArrayList<>();
            for (MaquinasAutomataAnfra maquina : maquinas) {
                if (maquina.getActiva() == null || maquina.getActiva() != 1) {
                    continue;
                }

                if (maquina.getNumMaquina() == null || maquina.getIdMemoria() == null) {
                    continue;
                }

                MemoriaAutomataAnfra memoria = memoriaPorId.get(maquina.getIdMemoria());
                if (memoria == null) {
                    continue;
                }

                configuraciones.add(new ConfiguracionMaquinaPLC(
                        maquina.getCodMaq() != null ? maquina.getCodMaq().trim() : String.valueOf(maquina.getNumMaquina()),
                        valorNoNulo(memoria.getOffsetAuto()),
                        valorNoNulo(memoria.getBitAuto()),
                        valorNoNulo(memoria.getOffsetManu()),
                        valorNoNulo(memoria.getBitManu())
                ));
            }

            configuraciones.sort(Comparator.comparing(c -> c.idMaquina));
            return configuraciones;
        } catch (RepositorioException e) {
            throw new RuntimeException("No se ha podido cargar la configuracion FINS desde la BBDD.", e);
        }
    }

    private static int valorNoNulo(Integer valor) {
        if (valor == null) {
            throw new IllegalStateException("La configuracion FINS no tiene todos los campos necesarios.");
        }
        return valor;
    }
}