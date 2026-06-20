package scada.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import repositorio.Identificable;

@Entity
@Table(name = "Automata_OEE_Bono_Resumen_ANFRA")
public class OEEBonoResumen implements Identificable {

    @Id
    @Column(name = "id", nullable = false, length = 80)
    private String id;

    @Column(name = "idDocBono", nullable = false, length = 30)
    private String idDocBono;

    @Column(name = "numMaquina", nullable = false, length = 20)
    private String numMaquina;

    @Column(name = "fechaInicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fechaFin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "piezasProducidas", nullable = false)
    private int piezasProducidas;

    @Column(name = "piezasBuenas", nullable = false)
    private int piezasBuenas;

    @Column(name = "piezasRevision", nullable = false)
    private int piezasRevision;

    @Column(name = "piezasScrap", nullable = false)
    private int piezasScrap;

    @Column(name = "tiempoCicloTeoricoSeg", nullable = false)
    private double tiempoCicloTeoricoSeg;

    @Column(name = "tiempoCicloMedioSeg", nullable = false)
    private double tiempoCicloMedioSeg;

    @Column(name = "tiempoCicloStdDevSeg", nullable = false)
    private double tiempoCicloStdDevSeg;

    @Column(name = "minutosAutomatico", nullable = false)
    private double minutosAutomatico;

    @Column(name = "minutosManual", nullable = false)
    private double minutosManual;

    @Column(name = "minutosParo", nullable = false)
    private double minutosParo;

    @Column(name = "disponibilidad", nullable = false)
    private double disponibilidad;

    @Column(name = "rendimiento", nullable = false)
    private double rendimiento;

    @Column(name = "calidad", nullable = false)
    private double calidad;

    @Column(name = "oeeTotal", nullable = false)
    private double oeeTotal;

    @Column(name = "fechaCalculo", nullable = false)
    private LocalDateTime fechaCalculo;

    public OEEBonoResumen() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getIdDocBono() {
        return idDocBono;
    }

    public void setIdDocBono(String idDocBono) {
        this.idDocBono = idDocBono;
    }

    public String getNumMaquina() {
        return numMaquina;
    }

    public void setNumMaquina(String numMaquina) {
        this.numMaquina = numMaquina;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getPiezasProducidas() {
        return piezasProducidas;
    }

    public void setPiezasProducidas(int piezasProducidas) {
        this.piezasProducidas = piezasProducidas;
    }

    public int getPiezasBuenas() {
        return piezasBuenas;
    }

    public void setPiezasBuenas(int piezasBuenas) {
        this.piezasBuenas = piezasBuenas;
    }

    public int getPiezasRevision() {
        return piezasRevision;
    }

    public void setPiezasRevision(int piezasRevision) {
        this.piezasRevision = piezasRevision;
    }

    public int getPiezasScrap() {
        return piezasScrap;
    }

    public void setPiezasScrap(int piezasScrap) {
        this.piezasScrap = piezasScrap;
    }

    public double getTiempoCicloTeoricoSeg() {
        return tiempoCicloTeoricoSeg;
    }

    public void setTiempoCicloTeoricoSeg(double tiempoCicloTeoricoSeg) {
        this.tiempoCicloTeoricoSeg = tiempoCicloTeoricoSeg;
    }

    public double getTiempoCicloMedioSeg() {
        return tiempoCicloMedioSeg;
    }

    public void setTiempoCicloMedioSeg(double tiempoCicloMedioSeg) {
        this.tiempoCicloMedioSeg = tiempoCicloMedioSeg;
    }

    public double getTiempoCicloStdDevSeg() {
        return tiempoCicloStdDevSeg;
    }

    public void setTiempoCicloStdDevSeg(double tiempoCicloStdDevSeg) {
        this.tiempoCicloStdDevSeg = tiempoCicloStdDevSeg;
    }

    public double getMinutosAutomatico() {
        return minutosAutomatico;
    }

    public void setMinutosAutomatico(double minutosAutomatico) {
        this.minutosAutomatico = minutosAutomatico;
    }

    public double getMinutosManual() {
        return minutosManual;
    }

    public void setMinutosManual(double minutosManual) {
        this.minutosManual = minutosManual;
    }

    public double getMinutosParo() {
        return minutosParo;
    }

    public void setMinutosParo(double minutosParo) {
        this.minutosParo = minutosParo;
    }

    public double getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(double disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public double getRendimiento() {
        return rendimiento;
    }

    public void setRendimiento(double rendimiento) {
        this.rendimiento = rendimiento;
    }

    public double getCalidad() {
        return calidad;
    }

    public void setCalidad(double calidad) {
        this.calidad = calidad;
    }

    public double getOeeTotal() {
        return oeeTotal;
    }

    public void setOeeTotal(double oeeTotal) {
        this.oeeTotal = oeeTotal;
    }

    public LocalDateTime getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDateTime fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }
}
