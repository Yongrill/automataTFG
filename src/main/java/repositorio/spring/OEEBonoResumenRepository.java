package repositorio.spring;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import scada.modelo.OEEBonoResumen;

@Repository
public interface OEEBonoResumenRepository extends JpaRepository<OEEBonoResumen, String> {

    OEEBonoResumen findByIdDocBono(String idDocBono);

    List<OEEBonoResumen> findByNumMaquina(String numMaquina);
}
