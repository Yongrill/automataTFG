package produccion.repositorio;

import java.util.List;

import produccion.modelo.MoldesVariantesParIny;
import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;

public interface RepositorioMoldesVariantesParInyAdHoc {
    
    /**
     * Busca los parametros de inyeccion por maquina, molde y variante.
     * Como el material puede no estar disponible inmediatamente o para flexibilizar la busqueda, 
     * lo filtramos por aquellos parametros.
     */
    List<MoldesVariantesParIny> buscarPorMaquinaMoldeYVariante(String idMaquina, Long idMolde, Long idVariante) throws RepositorioException, EntidadNoEncontrada;
    
}
