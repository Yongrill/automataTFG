package plc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import dominio.modelo.ConfiguracionMaquinaPLC;
import dominio.modelo.Maquina;
import servicio.ConfiguracionFinsPlcServicio;
import utils.PropertiesReader;

/**
 * Programa de prueba para leer el PLC OMRON via FINS y volcar la informacion a un TXT.
 */
public final class PruebaLecturaFinsTxt {
    private static final String PROPERTIES_FILE = "plc.properties";
    private static final String DEFAULT_PLC_IP = "127.0.0.1";
    private static final int DEFAULT_PLC_PORT = 9600;
    private static final int DEFAULT_PLC_NODE = 2;
    private static final int DEFAULT_TIMEOUT_MS = 2000;
    private static final String DEFAULT_CIO_MEMORY_AREA_HEX = "B0";
    private static final int DEFAULT_CIO_START_WORD = 0;
    private static final int DEFAULT_CIO_WORDS_TO_READ = 4;

    private static final Path SALIDA = Paths.get("target", "lecturas", "lectura_fins.txt");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int MUESTRAS = 6;
    private static final long PAUSA_ENTRE_MUESTRAS_MS = 1000L;
    private static final LectorTiempoCicloEm LECTOR_EM = new LectorTiempoCicloEm();
    private static final ConfiguracionFinsPlcServicio CONFIGURACION_SERVICIO = new ConfiguracionFinsPlcServicio();
    private static final Map<String, Boolean> ULTIMO_CONTADOR_POR_MAQUINA = new HashMap<>();
    private static final Map<String, Long> ULTIMA_MARCA_PULSO_POR_MAQUINA = new HashMap<>();
    private static final Map<String, Double> ULTIMO_CICLO_POR_MAQUINA = new HashMap<>();

    private PruebaLecturaFinsTxt() {
    }

    public static void main(String[] args) {
        ClienteFinsOmron cliente = crearClienteFins();
        List<ConfiguracionMaquinaPLC> configuraciones = CONFIGURACION_SERVICIO.obtenerConfiguracionesMaquinaCio();
        StringBuilder contenido = new StringBuilder();

        try {
            for (int i = 0; i < MUESTRAS; i++) {
                try {
                    byte[] datosCrudos = cliente.leerDatosCioCrudos();
                    contenido.append("Muestra ").append(i + 1)
                            .append(" - ")
                            .append(LocalDateTime.now().format(FORMATO_FECHA))
                            .append(System.lineSeparator());
                    contenido.append(construirInforme(cliente, datosCrudos, configuraciones));
                } catch (RuntimeException exMuestra) {
                    contenido.append(construirInformeErrorMuestra(i + 1, exMuestra));
                }

                esperarEntreMuestras();
            }
        } catch (RuntimeException ex) {
            contenido.setLength(0);
            contenido.append("ERROR EN LA PRUEBA: ").append(ex.getMessage()).append(System.lineSeparator());
        }

        escribirArchivo(contenido.toString());
        System.out.println("Informe escrito en: " + SALIDA.toAbsolutePath());
    }

    private static String construirInforme(ClienteFinsOmron cliente,
                                           byte[] datosCrudos,
                                           List<ConfiguracionMaquinaPLC> configuraciones) {
        List<Maquina> maquinas = cliente.decodificarCio(datosCrudos, configuraciones);
        long marcaMuestraMs = System.currentTimeMillis();
        Map<Integer, Double> tiemposCicloReales = LECTOR_EM.leerTiempoCicloPorMaquina();

        StringBuilder builder = new StringBuilder();
        builder.append(construirEstadoActualPorMaquina(maquinas, configuraciones, marcaMuestraMs, tiemposCicloReales));
        builder.append(System.lineSeparator());
        return builder.toString();
    }

    private static String construirEstadoActualPorMaquina(List<Maquina> maquinas,
                                  List<ConfiguracionMaquinaPLC> configuraciones,
                                  long marcaMuestraMs,
                                  Map<Integer, Double> tiemposCicloReales) {
        StringBuilder builder = new StringBuilder();
        builder.append("numMaquina\tidestado\ttiempoCiclo")
            .append(System.lineSeparator());

        Map<String, Maquina> maquinaPorId = new HashMap<>();
        for (Maquina maquina : maquinas) {
            maquinaPorId.put(maquina.getId(), maquina);
        }

        for (ConfiguracionMaquinaPLC configuracion : configuraciones) {
            Maquina maquina = maquinaPorId.get(configuracion.idMaquina);
            if (maquina == null) {
                continue;
            }

            boolean contadorActual = maquina.getContadorPiezas() > 0;
            boolean contadorAnterior = ULTIMO_CONTADOR_POR_MAQUINA.getOrDefault(maquina.getId(), false);
            double tiempoCiclo = ULTIMO_CICLO_POR_MAQUINA.getOrDefault(maquina.getId(), 0.0d);

            if (contadorActual && !contadorAnterior) {
                Long marcaAnterior = ULTIMA_MARCA_PULSO_POR_MAQUINA.get(maquina.getId());
                if (marcaAnterior != null) {
                    tiempoCiclo = (marcaMuestraMs - marcaAnterior) / 1000.0d;
                }
                ULTIMA_MARCA_PULSO_POR_MAQUINA.put(maquina.getId(), marcaMuestraMs);
                ULTIMO_CICLO_POR_MAQUINA.put(maquina.getId(), tiempoCiclo);
            }

            ULTIMO_CONTADOR_POR_MAQUINA.put(maquina.getId(), contadorActual);

            int numMaquina = Integer.parseInt(configuracion.idMaquina);
            int idEstado = maquina.getCodigoEstado();
            double tiempoFinal = tiemposCicloReales.getOrDefault(numMaquina, tiempoCiclo);

            builder.append(numMaquina)
                    .append("\t")
                    .append(idEstado)
                    .append("\t")
                    .append(formatearTiempoCiclo(tiempoFinal))
                    .append(System.lineSeparator());
        }

        return builder.toString();
    }

    private static String construirInformeErrorMuestra(int indiceMuestra, RuntimeException ex) {
        StringBuilder builder = new StringBuilder();
        builder.append("Muestra ").append(indiceMuestra).append(System.lineSeparator());
        builder.append("Fecha de lectura: ")
                .append(LocalDateTime.now().format(FORMATO_FECHA))
                .append(System.lineSeparator());
        builder.append("ERROR AL LEER EL PLC EN ESTA MUESTRA").append(System.lineSeparator());
        builder.append(ex.getClass().getSimpleName()).append(": ").append(ex.getMessage()).append(System.lineSeparator());
        builder.append(System.lineSeparator());
        return builder.toString();
    }

    private static String formatearTiempoCiclo(double tiempoCiclo) {
        if (Math.abs(tiempoCiclo) < 0.05d) {
            return "0";
        }
        return String.format("%.1f", tiempoCiclo);
    }

    private static void esperarEntreMuestras() {
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(PAUSA_ENTRE_MUESTRAS_MS));
    }

    private static void escribirArchivo(String contenido) {
        try {
            Files.createDirectories(SALIDA.getParent());
            Files.writeString(SALIDA, contenido, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("No se ha podido escribir el fichero de salida: " + SALIDA, e);
        }
    }

    private static ClienteFinsOmron crearClienteFins() {
        try {
            PropertiesReader reader = new PropertiesReader(PROPERTIES_FILE);
            String ip = valorConDefecto(reader.getProperty("plc.ip"), DEFAULT_PLC_IP);
            int port = parseEntero(valorConDefecto(reader.getProperty("plc.port"), String.valueOf(DEFAULT_PLC_PORT)), DEFAULT_PLC_PORT);
            int node = parseEntero(valorConDefecto(reader.getProperty("plc.node"), String.valueOf(DEFAULT_PLC_NODE)), DEFAULT_PLC_NODE);
            int timeoutMs = parseEntero(valorConDefecto(reader.getProperty("plc.timeoutMs"), String.valueOf(DEFAULT_TIMEOUT_MS)), DEFAULT_TIMEOUT_MS);
            int memoryAreaCode = parseHexEntero(valorConDefecto(reader.getProperty("plc.cio.memoryAreaHex"), DEFAULT_CIO_MEMORY_AREA_HEX), 0xB0);
            int startWord = parseEntero(valorConDefecto(reader.getProperty("plc.cio.startWord"), String.valueOf(DEFAULT_CIO_START_WORD)), DEFAULT_CIO_START_WORD);
            int wordsToRead = parseEntero(valorConDefecto(reader.getProperty("plc.cio.wordsToRead"), String.valueOf(DEFAULT_CIO_WORDS_TO_READ)), DEFAULT_CIO_WORDS_TO_READ);
            return new ClienteFinsOmron(ip, port, node, timeoutMs, memoryAreaCode, startWord, wordsToRead);
        } catch (IOException e) {
            throw new RuntimeException("No se ha podido cargar la configuracion PLC para la prueba TXT.", e);
        }
    }

    private static String valorConDefecto(String valor, String valorPorDefecto) {
        if (valor == null || valor.trim().isEmpty()) {
            return valorPorDefecto;
        }

        String valorLimpio = valor.trim();
        if (!valorLimpio.startsWith("${") || !valorLimpio.endsWith("}")) {
            return valorLimpio;
        }

        String expresion = valorLimpio.substring(2, valorLimpio.length() - 1);
        String[] partes = expresion.split(":", 2);
        String valorEntorno = System.getenv(partes[0]);
        if (valorEntorno != null && !valorEntorno.trim().isEmpty()) {
            return valorEntorno.trim();
        }

        return partes.length > 1 ? partes[1] : valorPorDefecto;
    }

    private static int parseEntero(String valor, int valorPorDefecto) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException ex) {
            return valorPorDefecto;
        }
    }

    private static int parseHexEntero(String valor, int valorPorDefecto) {
        try {
            return Integer.parseInt(valor, 16);
        } catch (NumberFormatException ex) {
            return valorPorDefecto;
        }
    }
}
