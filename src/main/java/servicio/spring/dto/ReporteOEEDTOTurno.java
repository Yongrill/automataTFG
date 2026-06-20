package servicio.spring.dto;

public class ReporteOEEDTOTurno {
    private String turno; // "MAÑANA", "TARDE", "NOCHE"
    
    private double disponibilidad;
    private double rendimiento;
    private double calidad;
    private double oeeTotal;

    private double minutosTotalesAutomata;
    private double minutosEnAutomatico;
    private int inyectadasTeoricas;
    private int inyectadasReales;
    private int totalPiezasBuenas;
    private int totalPiezasProducidas;

    public ReporteOEEDTOTurno() {}

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public double getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(double disponibilidad) { this.disponibilidad = disponibilidad; }

    public double getRendimiento() { return rendimiento; }
    public void setRendimiento(double rendimiento) { this.rendimiento = rendimiento; }

    public double getCalidad() { return calidad; }
    public void setCalidad(double calidad) { this.calidad = calidad; }

    public double getOeeTotal() { return oeeTotal; }
    public void setOeeTotal(double oeeTotal) { this.oeeTotal = oeeTotal; }

    public double getMinutosTotalesAutomata() { return minutosTotalesAutomata; }
    public void setMinutosTotalesAutomata(double minutosTotalesAutomata) { this.minutosTotalesAutomata = minutosTotalesAutomata; }

    public double getMinutosEnAutomatico() { return minutosEnAutomatico; }
    public void setMinutosEnAutomatico(double minutosEnAutomatico) { this.minutosEnAutomatico = minutosEnAutomatico; }

    public int getInyectadasTeoricas() { return inyectadasTeoricas; }
    public void setInyectadasTeoricas(int inyectadasTeoricas) { this.inyectadasTeoricas = inyectadasTeoricas; }

    public int getInyectadasReales() { return inyectadasReales; }
    public void setInyectadasReales(int inyectadasReales) { this.inyectadasReales = inyectadasReales; }

    public int getTotalPiezasBuenas() { return totalPiezasBuenas; }
    public void setTotalPiezasBuenas(int totalPiezasBuenas) { this.totalPiezasBuenas = totalPiezasBuenas; }

    public int getTotalPiezasProducidas() { return totalPiezasProducidas; }
    public void setTotalPiezasProducidas(int totalPiezasProducidas) { this.totalPiezasProducidas = totalPiezasProducidas; }
}