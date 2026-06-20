package repositorio.spring;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import scada.modelo.HistoricoProduccion;

@Repository
public interface HistoricoProduccionRepository extends JpaRepository<HistoricoProduccion, String> {
    List<HistoricoProduccion> findByIdDocBono(String idDocBono);
}
