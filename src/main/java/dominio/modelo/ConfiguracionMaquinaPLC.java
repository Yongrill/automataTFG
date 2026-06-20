package dominio.modelo;

public final class ConfiguracionMaquinaPLC {
    public final String idMaquina;
    public final int offsetAuto;
    public final int bitAuto;
    public final int offsetManu;
    public final int bitManu;

    public ConfiguracionMaquinaPLC(String idMaquina, int offsetAuto, int bitAuto, int offsetManu, int bitManu) {
        this.idMaquina = idMaquina;
        this.offsetAuto = offsetAuto;
        this.bitAuto = bitAuto;
        this.offsetManu = offsetManu;
        this.bitManu = bitManu;
    }
}