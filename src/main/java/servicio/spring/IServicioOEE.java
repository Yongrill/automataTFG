package servicio.spring;

import servicio.spring.dto.ReporteOEEDTO;

/**
 * Interfaz para la capa de Inteligencia de Negocio que calcula el OEE
 */
public interface IServicioOEE {
    
    /**
     * Calcula los porcentajes de OEE para un bono específico cruzando datos del SCADA y del ERP.
     * @param idDocBono ID del bono en producción
     * @return El DTO con la disponibilidad, rendimiento, calidad y OEE Total.
     */
    ReporteOEEDTO calcularOEEPorBono(String idDocBono);

}
