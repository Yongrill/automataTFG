package repositorio.spring;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import scada.modelo.FichajeBono;

@Repository
public interface FichajeBonoRepository extends JpaRepository<FichajeBono, String> {
    List<FichajeBono> findByIdDocBonoOrderByFechaFichajeDesc(String idDocBono);
}