package produccion.repositorio;

import java.util.List;

import produccion.modelo.OrdenProduccionView;
import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;
import repositorio.RepositorioString;

public interface RepositorioOrdenProduccionViewAdHoc extends RepositorioString<OrdenProduccionView>{
	public List<OrdenProduccionView> obtenerVistaERP() throws RepositorioException, EntidadNoEncontrada;	
	public List<OrdenProduccionView> buscarPorIdOrden(Long idOrden) throws RepositorioException, EntidadNoEncontrada;	
	public List<OrdenProduccionView> buscarPorMatricula(String matricula) throws RepositorioException, EntidadNoEncontrada;    public List<String> obtenerIdsBonosPorOrdenNativo(Long idOrden) throws RepositorioException;
    public List<String> obtenerIdsBonosPorMaquinaNativo(String matricula) throws RepositorioException;}