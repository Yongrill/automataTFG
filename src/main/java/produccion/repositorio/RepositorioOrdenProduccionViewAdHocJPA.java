package produccion.repositorio;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery; // Importante para tipos seguros

import org.springframework.stereotype.Repository;

import produccion.modelo.OrdenProduccionView;
import repositorio.RepositorioException;
import utils.EntityManagerHelper;

@Repository
public class RepositorioOrdenProduccionViewAdHocJPA extends RepositorioOrdenProduccionViewJPA implements RepositorioOrdenProduccionViewAdHoc {

	@Override
	public List<OrdenProduccionView> obtenerVistaERP() throws RepositorioException {
		EntityManager em = EntityManagerHelper.getEntityManager();
		try {
			String jpql = "SELECT o FROM OrdenProduccionView o";

			TypedQuery<OrdenProduccionView> query = em.createQuery(jpql, OrdenProduccionView.class);
			
			return query.getResultList();

		} catch (Exception e) {
			throw new RepositorioException("Error al obtener la vista del ERP", e);
		} finally {
			EntityManagerHelper.closeEntityManager();
		}
	}
	@Override
	public List<OrdenProduccionView> buscarPorIdOrden(Long idOrden) throws RepositorioException {
		EntityManager em = EntityManagerHelper.getEntityManager();
		try {
			String jpql = "SELECT o FROM OrdenProduccionView o WHERE o.idOrden = :idOrden";
			TypedQuery<OrdenProduccionView> query = em.createQuery(jpql, OrdenProduccionView.class);
			query.setParameter("idOrden", idOrden);
			return query.getResultList();
		} catch (Exception e) {
			throw new RepositorioException("Error al buscar orden de produccion por idOrden", e);
		} finally {
			EntityManagerHelper.closeEntityManager();
		}
	}

	@Override
	public List<OrdenProduccionView> buscarPorMatricula(String matricula) throws RepositorioException {
		EntityManager em = EntityManagerHelper.getEntityManager();
		try {
			String jpql = "SELECT o FROM OrdenProduccionView o WHERE o.matricula = :matricula";
			TypedQuery<OrdenProduccionView> query = em.createQuery(jpql, OrdenProduccionView.class);
			query.setParameter("matricula", matricula);
			return query.getResultList();
		} catch (Exception e) {
			throw new RepositorioException("Error al buscar orden de produccion por matricula", e);
		} finally {
			EntityManagerHelper.closeEntityManager();
		}
	}

	@Override
	public List<String> obtenerIdsBonosPorOrdenNativo(Long idOrden) throws RepositorioException {
		EntityManager em = EntityManagerHelper.getEntityManager();
		try {
			// Buscar directamente idDocBono en la tabla Ordenes_Bonos (tabla directa, en lugar de vista pesada)
			String sql = "SELECT CONVERT(varchar(50), ob.IdDoc) " +
						 "FROM Ordenes_Bonos ob " +
						 "WHERE ob.IdOrden = ?1";
			return em.createNativeQuery(sql).setParameter(1, idOrden).getResultList();
		} catch (Exception e) {
			throw new RepositorioException("Error al buscar idDocBono por idOrden agrupado", e);
		} finally {
			EntityManagerHelper.closeEntityManager();
		}
	}

	@Override
	public List<String> obtenerIdsBonosPorMaquinaNativo(String matricula) throws RepositorioException {
		EntityManager em = EntityManagerHelper.getEntityManager();
		try {
			// Buscar los distintos idDocBono para esa maquina (optimizacion desde el historico directamente)
			String sql = "SELECT DISTINCT CONVERT(varchar(50), h.idDocBono) " +
						 "FROM Automata_HistoricoProduccion_ANFRA h " +
						 "WHERE h.numMaquina = ?1 AND h.idDocBono IS NOT NULL";
			return em.createNativeQuery(sql).setParameter(1, matricula).getResultList();
		} catch (Exception e) {
			throw new RepositorioException("Error al buscar idDocBono por maquina agrupado", e);
		} finally {
			EntityManagerHelper.closeEntityManager();
		}
	}
}