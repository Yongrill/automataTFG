package dominio.modelo;

import java.util.regex.Pattern;

/**
 * Entidad de dominio que representa una maquina del SCADA.
 */
public class Maquina {
    private static final Pattern PATRON_ID = Pattern.compile("[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*");

    private final String id;
    private EstadoMaquina estado;
    private int contadorPiezas;
    private double tiempoCiclo;
    private int inyectadas;
    private Long ultimaMarcaPiezaMs;

    public Maquina(String id) {
        this(id, EstadoMaquina.PARO);
    }

    public Maquina(String id, EstadoMaquina estadoInicial) {
        this(id, estadoInicial, 0, 0.0d);
    }

    public Maquina(String id, EstadoMaquina estadoInicial, int contadorPiezas, double tiempoCiclo) {
        this(id, estadoInicial, contadorPiezas, tiempoCiclo, 0);
    }

    public Maquina(String id, EstadoMaquina estadoInicial, int contadorPiezas, double tiempoCiclo, int inyectadas) {
        validarId(id);
        if (estadoInicial == null) {
            throw new IllegalArgumentException("El estado inicial es obligatorio.");
        }
        if (contadorPiezas < 0) {
            throw new IllegalArgumentException("El contador de piezas no puede ser negativo.");
        }
        if (tiempoCiclo < 0.0d) {
            throw new IllegalArgumentException("El tiempo de ciclo no puede ser negativo.");
        }
        if (inyectadas < 0) {
            throw new IllegalArgumentException("Las inyectadas no pueden ser negativas.");
        }
        this.id = id;
        this.estado = estadoInicial;
        this.contadorPiezas = contadorPiezas;
        this.tiempoCiclo = tiempoCiclo;
        this.inyectadas = inyectadas;
        this.ultimaMarcaPiezaMs = null;
    }

    /**
     * Registra una pieza con la marca temporal actual.
     */
    public void registrarPieza() {
        registrarPieza(System.currentTimeMillis());
    }

    /**
     * Registra una pieza y calcula el tiempo de ciclo usando la diferencia de timestamps.
     */
    public void registrarPieza(long marcaTiempoMs) {
        if (marcaTiempoMs < 0) {
            throw new IllegalArgumentException("La marca de tiempo no puede ser negativa.");
        }
        if (estado == EstadoMaquina.PARO) {
            throw new IllegalStateException("No se pueden registrar piezas con la maquina en PARO.");
        }

        if (ultimaMarcaPiezaMs != null) {
            long deltaMs = marcaTiempoMs - ultimaMarcaPiezaMs;
            if (deltaMs <= 0) {
                throw new IllegalArgumentException("La marca de tiempo debe ser creciente entre piezas.");
            }
            this.tiempoCiclo = deltaMs / 1000.0d;
        }

        this.contadorPiezas++;
        this.ultimaMarcaPiezaMs = marcaTiempoMs;
    }

    /**
     * Cambia el estado operativo de la maquina.
     */
    public void cambiarEstado(EstadoMaquina nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo.");
        }
        this.estado = nuevoEstado;

        if (nuevoEstado == EstadoMaquina.PARO) {
            this.ultimaMarcaPiezaMs = null;
            this.tiempoCiclo = 0.0d;
        }
    }

    public void cambiarEstadoDesdeCodigo(int codigoEstado) {
        cambiarEstado(EstadoMaquina.desdeCodigo(codigoEstado));
    }

    public String getId() {
        return id;
    }

    public EstadoMaquina getEstado() {
        return estado;
    }

    public int getCodigoEstado() {
        return estado.getCodigo();
    }

    public int getContadorPiezas() {
        return contadorPiezas;
    }

    public int getInyectadas() {
        return inyectadas;
    }

    public double getTiempoCiclo() {
        return tiempoCiclo;
    }

    private static void validarId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El id de la maquina es obligatorio.");
        }

        String idNormalizado = id.trim();
        if (!PATRON_ID.matcher(idNormalizado).matches()) {
            throw new IllegalArgumentException("Formato de id de maquina invalido: " + id);
        }
    }
}
