package dominio.puertos.salida;

import dominio.modelo.IdMaquina;

/**
 * Puerto de Salida (Outbound Port): 
 * Define cómo el dominio envía directivas y comandos de control a los dispositivos físicos subyacentes (PLCs, Controladores).
 * Ej. MQTT, MODBUS u OPC-UA implementarán este puerto en la capa de infraestructura.
 */
public interface ClienteDispositivoSCADA {
    
    /**
     * Ordena a un dispositivo cesar operaciones inmediatamente.
     */
    void enviarComandoParadaCritica(IdMaquina id);
    
    /**
     * Envía una señal de reinicio a la máquina física.
     */
    void reiniciarDispositivo(IdMaquina id);
}
