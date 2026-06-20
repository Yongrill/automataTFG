package dominio.puertos.salida;

import dominio.modelo.IdMaquina;
import dominio.modelo.Maquina;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de Salida (Outbound Port): 
 * Define el contrato de persistencia para el agregado Máquina.
 * Permite a la Inyección de Dependencias usar BDs SQL o NoSQL más adelante sin acoplar el Dominio.
 */
public interface RepositorioMaquina {
    
    /**
     * Persiste o actualiza el estado completo de la máquina.
     */
    void guardar(Maquina maquina);
    
    /**
     * Recupera una máquina usando su ID de dominio.
     */
    Optional<Maquina> buscarPorId(IdMaquina id);
    
    /**
     * Devuelve una lista de todas las máquinas conectadas.
     */
    List<Maquina> buscarTodas();
}
