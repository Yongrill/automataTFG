package produccion.repositorio;

import produccion.modelo.MoldesVariantesParIny;
import repositorio.RepositorioJPA;

public class RepositorioMoldesVariantesParInyJPA extends RepositorioJPA<MoldesVariantesParIny> {

    @Override
    public Class<MoldesVariantesParIny> getClase() {
        return MoldesVariantesParIny.class;
    }
}
