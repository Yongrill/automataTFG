package produccion.modelo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import repositorio.Identificable;

@Entity
@Table(name = "MoldesVariantesParIny_ANFRA")
@IdClass(MoldesVariantesParInyId.class)
public class MoldesVariantesParIny implements Identificable {

    @Id
    @Column(name = "idMolde")
    private Long idMolde;

    @Id
    @Column(name = "idVariante")
    private Long idVariante;

    @Id
    @Column(name = "idMaterial")
    private String idMaterial;

    @Id
    @Column(name = "idMaquina")
    private String idMaquina;

    @Column(name = "inyMin")
    private Double inyMin;

    @Column(name = "sel")
    private Integer sel;

    @Column(name = "horasCambioMolde")
    private Double horasCambioMolde;

    @Column(name = "horasCambioPostizo")
    private Double horasCambioPostizo;

    @Column(name = "horasCambioColor")
    private Double horasCambioColor;

    @Column(name = "idDoc")
    private String idDoc;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "InsertUpdate")
    private Integer insertUpdate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fechaInsertUpdate")
    private Date fechaInsertUpdate;

    public MoldesVariantesParIny() {
    }

    @Override
    public String getId() {
        return idMolde + "-" + idVariante + "-" + idMaterial + "-" + idMaquina;
    }

    @Override
    public void setId(String id) {
        // No implementado para claves compuestas en este caso simple
    }

    // Getters y setters
    public Long getIdMolde() { return idMolde; }
    public void setIdMolde(Long idMolde) { this.idMolde = idMolde; }

    public Long getIdVariante() { return idVariante; }
    public void setIdVariante(Long idVariante) { this.idVariante = idVariante; }

    public String getIdMaterial() { return idMaterial; }
    public void setIdMaterial(String idMaterial) { this.idMaterial = idMaterial; }

    public String getIdMaquina() { return idMaquina; }
    public void setIdMaquina(String idMaquina) { this.idMaquina = idMaquina; }

    public Double getInyMin() { return inyMin; }
    public void setInyMin(Double inyMin) { this.inyMin = inyMin; }

    public Integer getSel() { return sel; }
    public void setSel(Integer sel) { this.sel = sel; }

    public Double getHorasCambioMolde() { return horasCambioMolde; }
    public void setHorasCambioMolde(Double horasCambioMolde) { this.horasCambioMolde = horasCambioMolde; }

    public Double getHorasCambioPostizo() { return horasCambioPostizo; }
    public void setHorasCambioPostizo(Double horasCambioPostizo) { this.horasCambioPostizo = horasCambioPostizo; }

    public Double getHorasCambioColor() { return horasCambioColor; }
    public void setHorasCambioColor(Double horasCambioColor) { this.horasCambioColor = horasCambioColor; }

    public String getIdDoc() { return idDoc; }
    public void setIdDoc(String idDoc) { this.idDoc = idDoc; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public Integer getInsertUpdate() { return insertUpdate; }
    public void setInsertUpdate(Integer insertUpdate) { this.insertUpdate = insertUpdate; }

    public Date getFechaInsertUpdate() { return fechaInsertUpdate; }
    public void setFechaInsertUpdate(Date fechaInsertUpdate) { this.fechaInsertUpdate = fechaInsertUpdate; }
}
