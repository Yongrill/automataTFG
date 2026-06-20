package scada.modelo;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import repositorio.Identificable;

@Entity
@Table(name = "Automata_MemoriaAutomata_ANFRA")
public class MemoriaAutomataAnfra implements Identificable {

    @Id
    @Column(name = "idMemoria", nullable = false)
    private Integer idMemoria;

    @Column(name = "dmIni")
    private Integer dmIni;

    @Column(name = "dmIniInc")
    private Integer dmIniInc;

    @Column(name = "dmFinInc")
    private Integer dmFinInc;

    @Column(name = "offsetAuto", nullable = false)
    private Integer offsetAuto;

    @Column(name = "offsetManu", nullable = false)
    private Integer offsetManu;

    @Column(name = "bitAuto", nullable = false)
    private Integer bitAuto;

    @Column(name = "bitManu", nullable = false)
    private Integer bitManu;

    @Column(name = "idDoc")
    private Integer idDoc;

    @Column(name = "Usuario")
    private String usuario;

    @Column(name = "InsertUpdate")
    private String insertUpdate;

    @Column(name = "FechaInsertUpdate")
    private LocalDateTime fechaInsertUpdate;

    public MemoriaAutomataAnfra() {
    }

    @Override
    public String getId() {
        return idMemoria == null ? null : String.valueOf(idMemoria);
    }

    @Override
    public void setId(String id) {
        this.idMemoria = id == null ? null : Integer.valueOf(id);
    }

    public Integer getIdMemoria() {
        return idMemoria;
    }

    public void setIdMemoria(Integer idMemoria) {
        this.idMemoria = idMemoria;
    }

    public Integer getDmIni() {
        return dmIni;
    }

    public void setDmIni(Integer dmIni) {
        this.dmIni = dmIni;
    }

    public Integer getDmIniInc() {
        return dmIniInc;
    }

    public void setDmIniInc(Integer dmIniInc) {
        this.dmIniInc = dmIniInc;
    }

    public Integer getDmFinInc() {
        return dmFinInc;
    }

    public void setDmFinInc(Integer dmFinInc) {
        this.dmFinInc = dmFinInc;
    }

    public Integer getOffsetAuto() {
        return offsetAuto;
    }

    public void setOffsetAuto(Integer offsetAuto) {
        this.offsetAuto = offsetAuto;
    }

    public Integer getOffsetManu() {
        return offsetManu;
    }

    public void setOffsetManu(Integer offsetManu) {
        this.offsetManu = offsetManu;
    }

    public Integer getBitAuto() {
        return bitAuto;
    }

    public void setBitAuto(Integer bitAuto) {
        this.bitAuto = bitAuto;
    }

    public Integer getBitManu() {
        return bitManu;
    }

    public void setBitManu(Integer bitManu) {
        this.bitManu = bitManu;
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