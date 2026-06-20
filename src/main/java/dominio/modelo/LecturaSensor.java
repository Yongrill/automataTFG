package dominio.modelo;

import java.time.Instant;

/**
 * Value Object que encapsula una lectura telemétrica de un sensor (temperatura, rpms, humedad, etc).
 */
public final class LecturaSensor {
    private final String tipoMedicion;
    private final double valor;
    private final String unidad;
    private final Instant tiempoLectura;

    public LecturaSensor(String tipoMedicion, double valor, String unidad, Instant tiempoLectura) {
        if (tipoMedicion == null || tipoMedicion.isEmpty()) {
            throw new IllegalArgumentException("El tipo de medición es obligatorio.");
        }
        if (unidad == null || unidad.isEmpty()) {
            throw new IllegalArgumentException("La unidad de medida es obligatoria.");
        }
        if (tiempoLectura == null) {
            throw new IllegalArgumentException("El timestamp de la lectura es obligatorio.");
        }
        
        this.tipoMedicion = tipoMedicion;
        this.valor = valor;
        this.unidad = unidad;
        this.tiempoLectura = tiempoLectura;
    }

    public String getTipoMedicion() {
        return tipoMedicion;
    }

    public double getValor() {
        return valor;
    }

    public String getUnidad() {
        return unidad;
    }

    public Instant getTiempoLectura() {
        return tiempoLectura;
    }
}
