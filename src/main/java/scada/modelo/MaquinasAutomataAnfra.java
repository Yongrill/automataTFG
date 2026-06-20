package scada.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import repositorio.Identificable;

@Entity
@Table(name = "Automata_MaquinasAutomata_ANFRA")
public class MaquinasAutomataAnfra implements Identificable {

    @Id
    @Column(name = "idMaqAuto", nullable = false)
    private Integer idMaqAuto;

    @Column(name = "numMaquina", nullable = false)
    private Integer numMaquina;

    @Column(name = "idMemoria", nullable = false)
    private Integer idMemoria;

    @Column(name = "codMaq")
    private String codMaq;

    @Column(name = "activa", nullable = false)
    private Integer activa;

    @Column(name = "sufijoEquipo")
    private String sufijoEquipo;

    @Column(name = "numEquipo")
    private Integer numEquipo;

    @Column(name = "parada")
    private Integer parada;

    @Column(name = "idDoc")
    private Integer idDoc;

    @Column(name = "Usuario")
    private String usuario;

    @Column(name = "InsertUpdate")
    private String insertUpdate;

    @Column(name = "FechaInsertUpdate")
    private LocalDateTime fechaInsertUpdate;

    public MaquinasAutomataAnfra() {
    }

    @Override
    public String getId() {
        return idMaqAuto == null ? null : String.valueOf(idMaqAuto);
    }

    @Override
    public void setId(String id) {
        this.idMaqAuto = id == null ? null : Integer.valueOf(id);
    }

    public Integer getIdMaqAuto() {
        return idMaqAuto;
    }

    public void setIdMaqAuto(Integer idMaqAuto) {
        this.idMaqAuto = idMaqAuto;
    }

    public Integer getNumMaquina() {
        return numMaquina;
    }

    public void setNumMaquina(Integer numMaquina) {
        this.numMaquina = numMaquina;
    }

    public Integer getIdMemoria() {
        return idMemoria;
    }

    public void setIdMemoria(Integer idMemoria) {
        this.idMemoria = idMemoria;
    }

    public String getCodMaq() {
        return codMaq;
    }

    public void setCodMaq(String codMaq) {
        this.codMaq = codMaq;
    }

    public Integer getActiva() {
        return activa;
    }

    public void setActiva(Integer activa) {
        this.activa = activa;
    }

    public String getSufijoEquipo() {
        return sufijoEquipo;
    }

    public void setSufijoEquipo(String sufijoEquipo) {
        this.sufijoEquipo = sufijoEquipo;
    }

    public Integer getNumEquipo() {
        return numEquipo;
    }

    public void setNumEquipo(Integer numEquipo) {
        this.numEquipo = numEquipo;
    }

    public Integer getParada() {
        return parada;
    }

    public void setParada(Integer parada) {
        this.parada = parada;
    }

    public Integer getIdDoc() {
        return idDoc;
    }

    public void setIdDoc(Integer idDoc) {
        this.idDoc = idDoc;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getInsertUpdate() {
        return insertUpdate;
    }

    public void setInsertUpdate(String insertUpdate) {
        this.insertUpdate = insertUpdate;
    }

    public LocalDateTime getFechaInsertUpdate() {
        return fechaInsertUpdate;
    }

    public void setFechaInsertUpdate(LocalDateTime fechaInsertUpdate) {
        this.fechaInsertUpdate = fechaInsertUpdate;
    }
}