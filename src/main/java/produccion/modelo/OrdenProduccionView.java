package produccion.modelo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import repositorio.Identificable;

@Entity
@Table(name = "vPers_Produccion_AltaPaletizado_BonosPaletizado")
public class OrdenProduccionView implements Identificable {

    @Id
    @Column(name = "IdDocBono")
    private String idDocBono;

    @Column(name = "IdOrden")
    private Long idOrden;

    @Column(name = "IdBono")
    private Long idBono;

    @Column(name = "IdEstado")
    private int idEstado;

    @Column(name = "IdDoc")
    private String idDoc; 

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FechaOrden")
    private Date fechaOrden;

    @Column(name = "IdArticulo")
    private String idArticulo;

    @Column(name = "DescArticulo")
    private String descArticulo;

    @Column(name = "Unidad")
    private String unidad;

    @Column(name = "EanArticulo")
    private String eanArticulo;

    @Column(name = "NumLote")
    private String numLote;

    @Column(name = "Cantidad")
    private BigDecimal cantidad;

    @Column(name = "CantidadAlta")
    private BigDecimal cantidadAlta;

    @Column(name = "IdAlmacenPaletizado")
    private int idAlmacenPaletizado;

    @Column(name = "IdUbicacionPaletizado")
    private int idUbicacionPaletizado;

    @Column(name = "AprovisionadoFisico")
    private int aprovisionadoFisico;

    @Column(name = "Pers_IdAgrupacionBonos")
    private int persIdAgrupacionBonos;

    @Column(name = "Pers_enCurso")
    private int persEnCurso;

    @Column(name = "Molde")
    private Long molde;

    @Column(name = "Variante")
    private Long variante;

    @Column(name = "Pers_Prioridad")
    private Integer persPrioridad;

    @Column(name = "Pers_EnPausa")
    private int persEnPausa;

    @Column(name = "Matricula")
    private String matricula;

    @Column(name = "DescripMaquina")
    private String descripMaquina;

    @Column(name = "Area")
    private String area;

    @Column(name = "AprovisionadoPedido")
    private int aprovisionadoPedido;

    @Column(name = "diasRestantes")
    private int diasRestantes;

    @Column(name = "tipoCambio")
    private String tipoCambio;

    public OrdenProduccionView() {
    }

    @Override
    public String getId() {
        return idDocBono;
    }

    @Override
    public void setId(String id) {
        this.idDocBono = id;
    }

    public String getIdDocBono() { return idDocBono; }
    public void setIdDocBono(String idDocBono) { this.idDocBono = idDocBono; }

    public Long getIdOrden() { return idOrden; }
    public void setIdOrden(Long idOrden) { this.idOrden = idOrden; }

    public Long getIdBono() { return idBono; }
    public void setIdBono(Long idBono) { this.idBono = idBono; }

    public int getIdEstado() { return idEstado; }
    public void setIdEstado(int idEstado) { this.idEstado = idEstado; }

    public String getIdDoc() { return idDoc; }
    public void setIdDoc(String idDoc) { this.idDoc = idDoc; }

    public Date getFechaOrden() { return fechaOrden; }
    public void setFechaOrden(Date fechaOrden) { this.fechaOrden = fechaOrden; }

    public String getIdArticulo() { return idArticulo; }
    public void setIdArticulo(String idArticulo) { this.idArticulo = idArticulo; }

    public String getDescArticulo() { return descArticulo; }
    public void setDescArticulo(String descArticulo) { this.descArticulo = descArticulo; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public String getEanArticulo() { return eanArticulo; }
    public void setEanArticulo(String eanArticulo) { this.eanArticulo = eanArticulo; }

    public String getNumLote() { return numLote; }
    public void setNumLote(String numLote) { this.numLote = numLote; }

    public BigDecimal getCantidad() { return cantidad; }
    public void setCantidad(BigDecimal cantidad) { this.cantidad = cantidad; }

    public BigDecimal getCantidadAlta() { return cantidadAlta; }
    public void setCantidadAlta(BigDecimal cantidadAlta) { this.cantidadAlta = cantidadAlta; }

    public int getIdAlmacenPaletizado() { return idAlmacenPaletizado; }
    public void setIdAlmacenPaletizado(int idAlmacenPaletizado) { this.idAlmacenPaletizado = idAlmacenPaletizado; }

    public int getIdUbicacionPaletizado() { return idUbicacionPaletizado; }
    public void setIdUbicacionPaletizado(int idUbicacionPaletizado) { this.idUbicacionPaletizado = idUbicacionPaletizado; }

    public int getAprovisionadoFisico() { return aprovisionadoFisico; }
    public void setAprovisionadoFisico(int aprovisionadoFisico) { this.aprovisionadoFisico = aprovisionadoFisico; }

    public int getPersIdAgrupacionBonos() { return persIdAgrupacionBonos; }
    public void setPersIdAgrupacionBonos(int persIdAgrupacionBonos) { this.persIdAgrupacionBonos = persIdAgrupacionBonos; }

    public int getPersEnCurso() { return persEnCurso; }
    public void setPersEnCurso(int persEnCurso) { this.persEnCurso = persEnCurso; }

    public Long getMolde() { return molde; }
    public void setMolde(Long molde) { this.molde = molde; }

    public Long getVariante() { return variante; }
    public void setVariante(Long variante) { this.variante = variante; }

    public Integer getPersPrioridad() { return persPrioridad; }
    public void setPersPrioridad(Integer persPrioridad) { this.persPrioridad = persPrioridad; }

    public int getPersEnPausa() { return persEnPausa; }
    public void setPersEnPausa(int persEnPausa) { this.persEnPausa = persEnPausa; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getDescripMaquina() { return descripMaquina; }
    public void setDescripMaquina(String descripMaquina) { this.descripMaquina = descripMaquina; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public int getAprovisionadoPedido() { return aprovisionadoPedido; }
    public void setAprovisionadoPedido(int aprovisionadoPedido) { this.aprovisionadoPedido = aprovisionadoPedido; }

    public int getDiasRestantes() { return diasRestantes; }
    public void setDiasRestantes(int diasRestantes) { this.diasRestantes = diasRestantes; }

    public String getTipoCambio() { return tipoCambio; }
    public void setTipoCambio(String tipoCambio) { this.tipoCambio = tipoCambio; }
}