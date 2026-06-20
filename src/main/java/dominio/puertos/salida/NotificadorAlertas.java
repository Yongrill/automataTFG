package dominio.puertos.salida;

import dominio.modelo.IdMaquina;
import dominio.modelo.EstadoMaquina;

/**
 * Puerto de Salida (Outbound Port):
 * Encargado de la propagación de eventos críticos o alertas del sistema a componentes externos.
 * Por ejemplo: envio de correos, avisos de Telegram/Slack o notificaciones push.
 */
public interface NotificadorAlertas {
    
    /**
     * Notifica externamente el cambio de estado de una máquina, típicamente usado cuando hay estado de FALLA o ALARMA.
     */
    void notificarCambioEstadoCritico(IdMaquina id, EstadoMaquina nuevoEstado, String mensajeAnalisis);
}
