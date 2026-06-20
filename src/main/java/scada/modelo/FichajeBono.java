package scada.modelo;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import repositorio.Identificable;

@Entity
@Table(name = "FichajeBono")
public class FichajeBono implements Identificable {

    @Id
    @Column(name = "id", nullable = false, length = 80)
    private String id;

    @Column(name = "idDocBono", nullable = false, length = 30)
    private String idDocBono;

    @Column(name = "idEmpleado", nullable = false)
    private int idEmpleado;

    @Column(name = "tipoFichaje", nullable = false, length = 20)
    private String tipoFichaje;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    @Column(name = "fechaFichaje", nullable = false)
    private LocalDateTime fechaFichaje;

    public FichajeBono() {}

    public FichajeBono(String id, String idDocBono, int idEmpleado, String tipoFichaje, String observaciones, LocalDateTime fechaFichaje) {
        this.id = id;
        this.idDocBono = idDocBono;
        this.idEmpleado = idEmpleado;
        this.tipoFichaje = tipoFichaje;
        this.observaciones = observaciones;
        this.fechaFichaje = fechaFichaje;
    }

    @Override
    public String getId() { return id; }
    @Override
    public void setId(String id) { this.id = id; }

    public String getIdDocBono() { return idDocBono; }
    public void setIdDocBono(String idDocBono) { this.idDocBono = idDocBono; }

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getTipoFichaje() { return tipoFichaje; }
    public void setTipoFichaje(String tipoFichaje) { this.tipoFichaje = tipoFichaje; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaFichaje() { return fechaFichaje; }
    public void setFechaFichaje(LocalDateTime fechaFichaje) { this.fechaFichaje = fechaFichaje; }
}