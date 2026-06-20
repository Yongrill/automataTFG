package servicio.spring.dto;

public class ReporteOEEDTO {

    private String idDocBono;
    private String numLote;
    private String idArticulo;
    private String idMaquina;

    private double disponibilidad;
    private double rendimiento;
    private double calidad;
    private double oeeTotal;

    // Detalles adicionales para trazar el cálculo
    private double minutosTotalesAutomata;
    private double minutosEnAutomatico;
    private int inyectadasTeoricas;
    private int inyectadasReales;
    private int totalPiezasBuenas;
    private int totalPiezasProducidas;

    private java.util.List<ReporteOEEDTOTurno> turnos = new java.util.ArrayList<>();

    public ReporteOEEDTO() {
    }

    public java.util.List<ReporteOEEDTOTurno> getTurnos() { return turnos; }
    public void setTurnos(java.util.List<ReporteOEEDTOTurno> turnos) { this.turnos = turnos; }

    public String getIdDocBono() { return idDocBono; }
    public void setIdDocBono(String idDocBono) { this.idDocBono = idDocBono; }

    public String getNumLote() { return numLote; }
    public void setNumLote(String numLote) { this.numLote = numLote; }

    public String getIdArticulo() { return idArticulo; }
    public void setIdArticulo(String idArticulo) { this.idArticulo = idArticulo; }

    public String getIdMaquina() { return idMaquina; }
    public void setIdMaquina(String idMaquina) { this.idMaquina = idMaquina; }

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
