package servicio.spring;

/**
 * Interfaz para el servicio de orquestación central de producción.
 * Controla la lectura cíclica del autómata, gestión de la caché de flancos y la persistencia en BBDD.
 */
public interface IServicioAutomataProduccion {

    /**
     * Tarea programada (por ejemplo a 500ms) que escanea el PLC e intercepta cambios en 
     * el estado de la máquina o producción de piezas vinculadas a una orden.
     */
    void escanearPlc();

}
