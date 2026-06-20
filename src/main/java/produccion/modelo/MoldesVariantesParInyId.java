package produccion.modelo;

import java.io.Serializable;
import java.util.Objects;

public class MoldesVariantesParInyId implements Serializable {

    private Long idMolde;
    private Long idVariante;
    private String idMaterial;
    private String idMaquina;

    public MoldesVariantesParInyId() {
    }

    public MoldesVariantesParInyId(Long idMolde, Long idVariante, String idMaterial, String idMaquina) {
        this.idMolde = idMolde;
        this.idVariante = idVariante;
        this.idMaterial = idMaterial;
        this.idMaquina = idMaquina;
    }

    // Getters y Setters
    public Long getIdMolde() { return idMolde; }
    public void setIdMolde(Long idMolde) { this.idMolde = idMolde; }

    public Long getIdVariante() { return idVariante; }
    public void setIdVariante(Long idVariante) { this.idVariante = idVariante; }

    public String getIdMaterial() { return idMaterial; }
    public void setIdMaterial(String idMaterial) { this.idMaterial = idMaterial; }

    public String getIdMaquina() { return idMaquina; }
    public void setIdMaquina(String idMaquina) { this.idMaquina = idMaquina; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoldesVariantesParInyId)) return false;
        MoldesVariantesParInyId that = (MoldesVariantesParInyId) o;
        return Objects.equals(idMolde, that.idMolde) &&
               Objects.equals(idVariante, that.idVariante) &&
               Objects.equals(idMaterial, that.idMaterial) &&
               Objects.equals(idMaquina, that.idMaquina);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMolde, idVariante, idMaterial, idMaquina);
    }
}
