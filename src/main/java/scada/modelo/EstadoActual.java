package scada.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import repositorio.Identificable;

@Entity
@Table(name = "Automata_EstadoActual_ANFRA")
public class EstadoActual implements Identificable {

    @Id
    @Column(name = "numMaquina", nullable = false, length = 20)
    private String id;

    @Column(name = "idEstado", nullable = false)
    private int idEstado;

    @Column(name = "tiempoCiclo", nullable = false)
    private double tiempoCiclo;

    @Column(name = "inyectadas", nullable = false)
    private int inyectadas;
    
    @Column(name = "IdDocBono", length = 30) // Nuevo campo para ligarlo a la orden de producción actual
    private String idDocBono;

    @Column(name = "fechaInicioEstado")
    private LocalDateTime fechaInicioEstado;

    @Column(name = "fechaUltimaActualizacion", nullable = false)
    private LocalDateTime fechaUltimaActualizacion;

    public EstadoActual() {
    }

    public EstadoActual(String id, int idEstado, double tiempoCiclo, int inyectadas, String idDocBono, LocalDateTime fechaInicioEstado, LocalDateTime fechaUltimaActualizacion) {
        this.id = id;
        this.idEstado = idEstado;
        this.tiempoCiclo = tiempoCiclo;
        this.inyectadas = inyectadas;
        this.idDocBono = idDocBono;
        this.fechaInicioEstado = fechaInicioEstado;
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public double getTiempoCiclo() {
        return tiempoCiclo;
    }

    public void setTiempoCiclo(double tiempoCiclo) {
        this.tiempoCiclo = tiempoCiclo;
    }

    public int getInyectadas() {
        return inyectadas;
    }

    public void setInyectadas(int inyectadas) {
        this.inyectadas = inyectadas;
    }

    public LocalDateTime getFechaUltimaActualizacion() {
        return fechaUltimaActualizacion;
    }

    public void setFechaUltimaActualizacion(LocalDateTime fechaUltimaActualizacion) {
        this.fechaUltimaActualizacion = fechaUltimaActualizacion;
    }

    public LocalDateTime getFechaInicioEstado() {
        return fechaInicioEstado;
    }

    public void setFechaInicioEstado(LocalDateTime fechaInicioEstado) {
        this.fechaInicioEstado = fechaInicioEstado;
    }

    public String getIdDocBono() {
        return idDocBono;
    }

    public void setIdDocBono(String idDocBono) {
        this.idDocBono = idDocBono;
    }
}
