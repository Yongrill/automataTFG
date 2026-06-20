package plc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import utils.PropertiesReader;

/**
 * Lector FINS independiente para la memoria EM.
 * Se mantiene separado del cliente CIO para no reintroducir estado en la clase principal.
 */
public final class LectorTiempoCicloEm {
    private static final String PROPERTIES_FILE = "plc.properties";
    private static final String DEFAULT_PLC_IP = "127.0.0.1";
    private static final int DEFAULT_PLC_PORT = 9600;
    private static final int DEFAULT_PLC_NODE = 2;
    private static final int DEFAULT_TIMEOUT_MS = 2000;
    private static final String DEFAULT_EM_MEMORY_AREA_HEX = "A0";
    private static final int DEFAULT_EM_START_WORD = 1000;
    private static final int DEFAULT_EM_END_WORD = 29350;
    private static final int DEFAULT_EM_WORDS_PER_RECORD = 12;
    private static final int DEFAULT_EM_MAX_WORDS_PER_READ = 120;
    private static final boolean DEFAULT_EM_ENABLED = true;
    private static final int MAX_REGISTROS_A_LEER = 21;

    private static final int BYTES_POR_PALABRA = 2;
    private static final int FINS_HEADER_BYTES = 14;

    private final String plcIp;
    private final int plcPort;
    private final int plcNode;
    private final int timeoutMs;
    private final boolean emEnabled;
    private final int emMemoryAreaCode;
    private final int emStartWord;
    private final int emEndWord;
    private final int emWordsPerRecord;
    private final int emMaxWordsPerRead;

    public LectorTiempoCicloEm() {
        this(cargarConfiguracion());
    }

    private LectorTiempoCicloEm(Configuracion configuracion) {
        this.plcIp = configuracion.plcIp;
        this.plcPort = configuracion.plcPort;
        this.plcNode = configuracion.plcNode;
        this.timeoutMs = configuracion.timeoutMs;
        this.emEnabled = configuracion.emEnabled;
        this.emMemoryAreaCode = configuracion.emMemoryAreaCode;
        this.emStartWord = configuracion.emStartWord;
        this.emEndWord = configuracion.emEndWord;
        this.emWordsPerRecord = configuracion.emWordsPerRecord;
        this.emMaxWordsPerRead = configuracion.emMaxWordsPerRead;
    }

    public Map<Integer, Double> leerTiempoCicloPorMaquina() {
        if (!emEnabled) {
            return Collections.emptyMap();
        }

        try {
            Map<Integer, RegistroTiempoCicloEm> registros = leerUltimosRegistrosPorMaquina();
            if (registros.isEmpty()) {
                return Collections.emptyMap();
            }

            Map<Integer, Double> resultado = new HashMap<>();
            for (RegistroTiempoCicloEm registro : registros.values()) {
                resultado.put(registro.numMaquina, registro.tiempoCicloSegundos);
            }
            return resultado;
        } catch (RuntimeException ex) {
            return Collections.emptyMap();
        }
    }

    private Map<Integer, RegistroTiempoCicloEm> leerUltimosRegistrosPorMaquina() {
        if (emEndWord < emStartWord || emWordsPerRecord <= 0) {
            return Collections.emptyMap();
        }

        int totalWords = Math.min(emEndWord - emStartWord + 1, emWordsPerRecord * MAX_REGISTROS_A_LEER);
        int wordsPorLectura = Math.max(emWordsPerRecord, emMaxWordsPerRead);
        byte[] datosEm = new byte[totalWords * BYTES_POR_PALABRA];
        int offsetBytes = 0;

        for (int offsetWords = 0; offsetWords < totalWords; offsetWords += wordsPorLectura) {
            int chunkWords = Math.min(wordsPorLectura, totalWords - offsetWords);
            byte[] chunk = leerMemoriaPalabras(emMemoryAreaCode, emStartWord + offsetWords, chunkWords,
                    chunkWords * BYTES_POR_PALABRA);
            System.arraycopy(chunk, 0, datosEm, offsetBytes, chunk.length);
            offsetBytes += chunk.length;
        }

        int bytesPorRegistro = emWordsPerRecord * BYTES_POR_PALABRA;
        Map<Integer, RegistroTiempoCicloEm> resultado = new HashMap<>();

        for (int pos = 0; pos + bytesPorRegistro <= datosEm.length; pos += bytesPorRegistro) {
            RegistroTiempoCicloEm registro = parsearRegistro(datosEm, pos);
            if (registro == null) {
                continue;
            }

            RegistroTiempoCicloEm actual = resultado.get(registro.numMaquina);
            if (actual == null || registro.fechaRegistro.isAfter(actual.fechaRegistro)) {
                resultado.put(registro.numMaquina, registro);
            }
        }

        return resultado;
    }

    private RegistroTiempoCicloEm parsearRegistro(byte[] datosEm, int inicio) {
        int dia = leerPalabra(datosEm, inicio + 1 * BYTES_POR_PALABRA);
        int mes = leerPalabra(datosEm, inicio + 2 * BYTES_POR_PALABRA);
        int anio = leerPalabra(datosEm, inicio + 3 * BYTES_POR_PALABRA);
        int hora = leerPalabra(datosEm, inicio + 4 * BYTES_POR_PALABRA);
        int minuto = leerPalabra(datosEm, inicio + 5 * BYTES_POR_PALABRA);
        int tiempoCicloDecimas = leerPalabra(datosEm, inicio + 9 * BYTES_POR_PALABRA);
        int numMaquina = leerPalabra(datosEm, inicio + 11 * BYTES_POR_PALABRA);

        if (numMaquina <= 0 || dia <= 0 || mes <= 0) {
            return null;
        }

        LocalDateTime fechaRegistro;
        try {
            fechaRegistro = LocalDateTime.of(2000 + anio, mes, dia, hora, minuto);
        } catch (RuntimeException ex) {
            return null;
        }

        return new RegistroTiempoCicloEm(numMaquina, tiempoCicloDecimas / 10.0d, fechaRegistro);
    }

    private byte[] leerMemoriaPalabras(int memoryAreaCode, int startWord, int wordsToRead, int expectedDataBytes) {
        byte sid = (byte) (System.nanoTime() & 0xFF);
        byte[] request = construirPeticionMemoria(sid, memoryAreaCode, startWord, wordsToRead);

        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(timeoutMs);
            InetAddress ipPlc = InetAddress.getByName(plcIp);

            DatagramPacket peticion = new DatagramPacket(request, request.length, ipPlc, plcPort);
            socket.send(peticion);

            byte[] buffer = new byte[Math.max(256, FINS_HEADER_BYTES + expectedDataBytes + 32)];
            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
            socket.receive(respuesta);

            return extraerDatosFins(respuesta.getData(), respuesta.getLength(), expectedDataBytes);
        } catch (SocketTimeoutException e) {
            throw new RuntimeException("Timeout al esperar respuesta FINS del PLC " + plcIp + ":" + plcPort, e);
        } catch (IOException e) {
            throw new RuntimeException("Error de comunicacion FINS con el PLC " + plcIp + ":" + plcPort, e);
        }
    }

    private byte[] construirPeticionMemoria(byte sid, int memoryAreaCode, int startWord, int wordsToRead) {
        int inicioHigh = (startWord >> 8) & 0xFF;
        int inicioLow = startWord & 0xFF;
        int cantidadHigh = (wordsToRead >> 8) & 0xFF;
        int cantidadLow = wordsToRead & 0xFF;

        return new byte[] {
                (byte) 0x80,
                (byte) 0x00,
                (byte) 0x02,
                (byte) 0x00,
                (byte) plcNode,
                (byte) 0x00,
                (byte) 0x00,
                (byte) 0x01,
                (byte) 0x00,
                sid,
                (byte) 0x01,
                (byte) 0x01,
                (byte) memoryAreaCode,
                (byte) inicioHigh,
                (byte) inicioLow,
                (byte) 0x00,
                (byte) cantidadHigh,
                (byte) cantidadLow
        };
    }

    private static byte[] extraerDatosFins(byte[] tramaRespuesta, int longitud, int expectedDataBytes) {
        if (longitud < FINS_HEADER_BYTES) {
            throw new IllegalStateException("Respuesta FINS demasiado corta. Longitud recibida: " + longitud);
        }

        int endCode = ((tramaRespuesta[12] & 0xFF) << 8) | (tramaRespuesta[13] & 0xFF);
        if (endCode != 0) {
            throw new IllegalStateException("PLC devolvio End Code FINS distinto de 0: 0x"
                    + Integer.toHexString(endCode));
        }

        byte[] datos = new byte[expectedDataBytes];
        int bytesDisponibles = Math.max(0, longitud - FINS_HEADER_BYTES);
        int bytesACopiar = Math.min(expectedDataBytes, bytesDisponibles);
        System.arraycopy(tramaRespuesta, FINS_HEADER_BYTES, datos, 0, bytesACopiar);
        return datos;
    }

    private static int leerPalabra(byte[] buffer, int offset) {
        int high = buffer[offset] & 0xFF;
        int low = buffer[offset + 1] & 0xFF;
        return (high << 8) | low;
    }

    private static Configuracion cargarConfiguracion() {
        try {
            PropertiesReader reader = new PropertiesReader(PROPERTIES_FILE);
            return new Configuracion(
                    valorConDefecto(reader.getProperty("plc.ip"), DEFAULT_PLC_IP),
                    parseEntero(valorConDefecto(reader.getProperty("plc.port"), String.valueOf(DEFAULT_PLC_PORT)), DEFAULT_PLC_PORT),
                    parseEntero(valorConDefecto(reader.getProperty("plc.node"), String.valueOf(DEFAULT_PLC_NODE)), DEFAULT_PLC_NODE),
                    parseEntero(valorConDefecto(reader.getProperty("plc.timeoutMs"), String.valueOf(DEFAULT_TIMEOUT_MS)), DEFAULT_TIMEOUT_MS),
                    Boolean.parseBoolean(valorConDefecto(reader.getProperty("plc.em.enabled"), String.valueOf(DEFAULT_EM_ENABLED))),
                    parseHexEntero(valorConDefecto(reader.getProperty("plc.em.memoryAreaHex"), DEFAULT_EM_MEMORY_AREA_HEX), 0xA0),
                    parseEntero(valorConDefecto(reader.getProperty("plc.em.startWord"), String.valueOf(DEFAULT_EM_START_WORD)), DEFAULT_EM_START_WORD),
                    parseEntero(valorConDefecto(reader.getProperty("plc.em.endWord"), String.valueOf(DEFAULT_EM_END_WORD)), DEFAULT_EM_END_WORD),
                    parseEntero(valorConDefecto(reader.getProperty("plc.em.wordsPerRecord"), String.valueOf(DEFAULT_EM_WORDS_PER_RECORD)), DEFAULT_EM_WORDS_PER_RECORD),
                    parseEntero(valorConDefecto(reader.getProperty("plc.em.maxWordsPerRead"), String.valueOf(DEFAULT_EM_MAX_WORDS_PER_READ)), DEFAULT_EM_MAX_WORDS_PER_READ)
            );
        } catch (IOException e) {
            throw new RuntimeException("No se ha podido cargar la configuracion EM.", e);
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

    private static final class Configuracion {
        private final String plcIp;
        private final int plcPort;
        private final int plcNode;
        private final int timeoutMs;
        private final boolean emEnabled;
        private final int emMemoryAreaCode;
        private final int emStartWord;
        private final int emEndWord;
        private final int emWordsPerRecord;
        private final int emMaxWordsPerRead;

        private Configuracion(String plcIp, int plcPort, int plcNode, int timeoutMs, boolean emEnabled,
                              int emMemoryAreaCode, int emStartWord, int emEndWord,
                              int emWordsPerRecord, int emMaxWordsPerRead) {
            this.plcIp = plcIp;
            this.plcPort = plcPort;
            this.plcNode = plcNode;
            this.timeoutMs = timeoutMs;
            this.emEnabled = emEnabled;
            this.emMemoryAreaCode = emMemoryAreaCode;
            this.emStartWord = emStartWord;
            this.emEndWord = emEndWord;
            this.emWordsPerRecord = emWordsPerRecord;
            this.emMaxWordsPerRead = emMaxWordsPerRead;
        }
    }

    private static final class RegistroTiempoCicloEm {
        private final int numMaquina;
        private final double tiempoCicloSegundos;
        private final LocalDateTime fechaRegistro;

        private RegistroTiempoCicloEm(int numMaquina, double tiempoCicloSegundos, LocalDateTime fechaRegistro) {
            this.numMaquina = numMaquina;
            this.tiempoCicloSegundos = tiempoCicloSegundos;
            this.fechaRegistro = fechaRegistro;
        }
    }
}
