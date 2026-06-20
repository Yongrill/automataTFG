package dominio.modelo;

/**
 * Estados operativos de una máquina y su codificación de PLC.
 */
public enum EstadoMaquina {
    AUTOMATICO(0),
    MANUAL(1),
    PARO(2);

    private final int codigo;

    EstadoMaquina(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static EstadoMaquina desdeCodigo(int codigo) {
        for (EstadoMaquina estado : values()) {
            if (estado.codigo == codigo) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Codigo de estado no soportado: " + codigo);
    }
}
