package scada.repositorio;

import repositorio.RepositorioJPA;
import scada.modelo.MaquinasAutomataAnfra;

public class RepositorioMaquinasAutomataAnfraJPA extends RepositorioJPA<MaquinasAutomataAnfra> {

    @Override
    public Class<MaquinasAutomataAnfra> getClase() {
        return MaquinasAutomataAnfra.class;
    }
}