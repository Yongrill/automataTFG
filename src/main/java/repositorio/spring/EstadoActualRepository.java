package repositorio.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import scada.modelo.EstadoActual;

@Repository
public interface EstadoActualRepository extends JpaRepository<EstadoActual, String> {
}
