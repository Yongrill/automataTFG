package produccion.servicio;

import java.util.List;

import produccion.modelo.OrdenProduccionView;
import produccion.repositorio.RepositorioOrdenProduccionViewAdHocJPA;
import repositorio.EntidadNoEncontrada;
import repositorio.FactoriaRepositorios;
import repositorio.RepositorioException;

public class ServicioOrdenProduccionView implements IServicioOrdenProduccionView {

    private RepositorioOrdenProduccionViewAdHocJPA repositorio = FactoriaRepositorios.getRepositorio(OrdenProduccionView.class);

    @Override
    public List<OrdenProduccionView> obtenerVista() throws RepositorioException, EntidadNoEncontrada {
        return repositorio.obtenerVistaERP();
    }
}
