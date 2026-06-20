package scada.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import repositorio.Identificable;

@Entity
@Table(name = "Automata_HistoricoProduccion_ANFRA")
public class HistoricoProduccion implements Identificable {

    @Id
    @Column(name = "id", nullable = false, length = 40)
    private String id;

    @Column(name = "numMaquina", nullable = false, length = 20)
    private String numMaquina;

    @Column(name = "idEstado", nullable = false)
    private int idEstado;

    @Column(name = "tiempoCicloMedio", nullable = false)
    private double tiempoCicloMedio;

    @Column(name = "tiempoCicloMax", nullable = false)
    private double tiempoCicloMax;

    @Column(name = "tiempoCicloMin", nullable = false)
    private double tiempoCicloMin;

    @Column(name = "tiempoCicloStdDev", nullable = false)
    private double tiempoCicloStdDev;

    @Column(name = "inyectadas", nullable = false)
    private int inyectadas;
    
    @Column(name = "IdDocBono", length = 30) // Nuevo campo para ligarlo a la orden de producción actual
    private String idDocBono;

    @Column(name = "fechaEvento", nullable = false)
    private LocalDateTime fechaEvento;

    public HistoricoProduccion() {
    }

    public HistoricoProduccion(String id, String numMaquina, int idEstado, double tiempoCicloMedio,
                               double tiempoCicloMax, double tiempoCicloMin, double tiempoCicloStdDev,
                               int inyectadas, String idDocBono, LocalDateTime fechaEvento) {
        this.id = id;
        this.numMaquina = numMaquina;
        this.idEstado = idEstado;
        this.tiempoCicloMedio = tiempoCicloMedio;
        this.tiempoCicloMax = tiempoCicloMax;
        this.tiempoCicloMin = tiempoCicloMin;
        this.tiempoCicloStdDev = tiempoCicloStdDev;
        this.inyectadas = inyectadas;
        this.idDocBono = idDocBono;
        this.fechaEvento = fechaEvento;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getNumMaquina() {
        return numMaquina;
    }

    public void setNumMaquina(String numMaquina) {
        this.numMaquina = numMaquina;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public double getTiempoCicloMedio() {
        return tiempoCicloMedio;
    }

    public void setTiempoCicloMedio(double tiempoCicloMedio) {
        this.tiempoCicloMedio = tiempoCicloMedio;
    }

    public double getTiempoCicloMax() {
        return tiempoCicloMax;
    }

    public void setTiempoCicloMax(double tiempoCicloMax) {
        this.tiempoCicloMax = tiempoCicloMax;
    }

    public double getTiempoCicloMin() {
        return tiempoCicloMin;
    }

    public void setTiempoCicloMin(double tiempoCicloMin) {
        this.tiempoCicloMin = tiempoCicloMin;
    }

    public double getTiempoCicloStdDev() {
        return tiempoCicloStdDev;
    }

    public void setTiempoCicloStdDev(double tiempoCicloStdDev) {
        this.tiempoCicloStdDev = tiempoCicloStdDev;
    }

    public int getInyectadas() {
        return inyectadas;
    }

    public void setInyectadas(int inyectadas) {
        this.inyectadas = inyectadas;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getIdDocBono() {
        return idDocBono;
    }

    public void setIdDocBono(String idDocBono) {
        this.idDocBono = idDocBono;
    }
}
