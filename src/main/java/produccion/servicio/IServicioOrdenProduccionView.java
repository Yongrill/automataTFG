package produccion.servicio;

import java.util.List;

import produccion.modelo.OrdenProduccionView;
import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;

public interface IServicioOrdenProduccionView {
        public List<OrdenProduccionView> obtenerVista() throws RepositorioException, EntidadNoEncontrada;
}
