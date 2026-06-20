package scada.repositorio;

import repositorio.RepositorioJPA;
import scada.modelo.MemoriaAutomataAnfra;

public class RepositorioMemoriaAutomataAnfraJPA extends RepositorioJPA<MemoriaAutomataAnfra> {

    @Override
    public Class<MemoriaAutomataAnfra> getClase() {
        return MemoriaAutomataAnfra.class;
    }
}