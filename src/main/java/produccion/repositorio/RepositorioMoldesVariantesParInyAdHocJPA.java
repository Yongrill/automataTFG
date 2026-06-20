package produccion.repositorio;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import produccion.modelo.MoldesVariantesParIny;
import repositorio.EntidadNoEncontrada;
import repositorio.RepositorioException;

@Repository
public class RepositorioMoldesVariantesParInyAdHocJPA extends RepositorioMoldesVariantesParInyJPA implements RepositorioMoldesVariantesParInyAdHoc {

    @PersistenceContext
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<MoldesVariantesParIny> buscarPorMaquinaMoldeYVariante(String idMaquina, Long idMolde, Long idVariante) throws RepositorioException, EntidadNoEncontrada {
        try {
            CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
            CriteriaQuery<MoldesVariantesParIny> cq = cb.createQuery(MoldesVariantesParIny.class);
            Root<MoldesVariantesParIny> root = cq.from(MoldesVariantesParIny.class);
            
            Predicate maquinaPred = cb.equal(root.get("idMaquina"), idMaquina);
            Predicate moldePred = cb.equal(root.get("idMolde"), idMolde);
            Predicate variantePred = cb.equal(root.get("idVariante"), idVariante);
            
            cq.where(cb.and(maquinaPred, moldePred, variantePred));
            
            TypedQuery<MoldesVariantesParIny> query = getEntityManager().createQuery(cq);
            return query.getResultList();
        } catch (Exception e) {
            throw new RepositorioException("Error al buscar parametros de inyeccion", e);
        }
    }
}
