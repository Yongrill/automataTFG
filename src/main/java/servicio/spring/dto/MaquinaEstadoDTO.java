package servicio.spring.dto;

public class MaquinaEstadoDTO {
    private final String id;
    private final int estado;
    private final int piezas;
    private final double tiempoCiclo;
    private final String bono;
    private final long tiempoEnEstado;

    public MaquinaEstadoDTO(String id, int estado, int piezas, double tiempoCiclo, String bono, long tiempoEnEstado) {
        this.id = id;
        this.estado = estado;
        this.piezas = piezas;
        this.tiempoCiclo = tiempoCiclo;
        this.bono = bono;
        this.tiempoEnEstado = tiempoEnEstado;
    }

    public String getId() {
        return id;
    }

    public int getEstado() {
        return estado;
    }

    public int getPiezas() {
        return piezas;
    }

    public double getTiempoCiclo() {
        return tiempoCiclo;
    }

    public String getBono() {
        return bono;
    }

    public long getTiempoEnEstado() {
        return tiempoEnEstado;
    }
}
