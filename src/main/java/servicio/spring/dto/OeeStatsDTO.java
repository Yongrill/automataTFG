package servicio.spring.dto;

public class OeeStatsDTO {
    private final String idMaquina;
    private final String idDocBono;
    private final int estado;
    private final double media;
    private final double max;
    private final double min;
    private final double stdDev;
    private final int totalInyectadas;
    private final String fechaEvento;

    public OeeStatsDTO(String idMaquina, String idDocBono, int estado, double media, double max, double min, double stdDev, int totalInyectadas, String fechaEvento) {
        this.idMaquina = idMaquina;
        this.idDocBono = idDocBono;
        this.estado = estado;
        this.media = media;
        this.max = max;
        this.min = min;
        this.stdDev = stdDev;
        this.totalInyectadas = totalInyectadas;
        this.fechaEvento = fechaEvento;
    }

    public String getIdMaquina() {
        return idMaquina;
    }

    public String getIdDocBono() {
        return idDocBono;
    }

    public int getEstado() {
        return estado;
    }

    public double getMedia() {
        return media;
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getStdDev() {
        return stdDev;
    }

    public int getTotalInyectadas() {
        return totalInyectadas;
    }

    public String getFechaEvento() {
        return fechaEvento;
    }
}
