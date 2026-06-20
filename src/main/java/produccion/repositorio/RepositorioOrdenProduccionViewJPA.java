package produccion.repositorio;

import produccion.modelo.OrdenProduccionView;
import repositorio.RepositorioJPA;

public class RepositorioOrdenProduccionViewJPA extends RepositorioJPA<OrdenProduccionView>{

	@Override
	public Class<OrdenProduccionView> getClase() {
		return OrdenProduccionView.class;
	}

}