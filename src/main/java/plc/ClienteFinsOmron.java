package plc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import dominio.modelo.ConfiguracionMaquinaPLC;
import dominio.modelo.EstadoMaquina;
import dominio.modelo.Maquina;

/**
 * Cliente UDP para lectura FINS de memoria CIO en un Automata OMRON.
 * La clase no conserva estado de proceso: cada llamada decodifica la muestra recibida
 * a partir de la configuracion que se le entrega.
 */
public class ClienteFinsOmron implements AutoCloseable {
    private static final int BYTES_POR_PALABRA = 2;
    private static final int TAMANO_CIO_BYTES = 8;
    private static final int FINS_HEADER_BYTES = 14;

    private final String plcIp;
    private final int plcPort;
    private final int plcNode;
    private final int timeoutMs;
    private final int cioStartWord;
    private final int cioWordsToRead;
    private final int cioMemoryAreaCode;
    private final InetAddress ipPlc;
    private final DatagramSocket socket;

    public ClienteFinsOmron(String plcIp,
                            int plcPort,
                            int plcNode,
                            int timeoutMs,
                            int cioMemoryAreaCode,
                            int cioStartWord,
                            int cioWordsToRead) {
        if (plcIp == null || plcIp.trim().isEmpty()) {
            throw new IllegalArgumentException("La IP del PLC no puede ser nula ni vacia.");
        }
        if (plcPort <= 0) {
            throw new IllegalArgumentException("El puerto del PLC debe ser mayor que 0.");
        }
        if (plcNode < 0) {
            throw new IllegalArgumentException("El nodo del PLC no puede ser negativo.");
        }
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("El timeout debe ser mayor que 0.");
        }
        if (cioMemoryAreaCode < 0 || cioMemoryAreaCode > 0xFF) {
            throw new IllegalArgumentException("El codigo de area CIO debe estar entre 0 y 255.");
        }
        if (cioStartWord < 0) {
            throw new IllegalArgumentException("La palabra inicial CIO no puede ser negativa.");
        }
        if (cioWordsToRead <= 0) {
            throw new IllegalArgumentException("La cantidad de palabras CIO a leer debe ser mayor que 0.");
        }

        this.plcIp = plcIp.trim();
        this.plcPort = plcPort;
        this.plcNode = plcNode;
        this.timeoutMs = timeoutMs;
        this.cioMemoryAreaCode = cioMemoryAreaCode;
        this.cioStartWord = cioStartWord;
        this.cioWordsToRead = cioWordsToRead;

        try {
            this.ipPlc = InetAddress.getByName(this.plcIp);
            this.socket = new DatagramSocket();
            this.socket.setSoTimeout(this.timeoutMs);
        } catch (SocketException e) {
            throw new RuntimeException("No se ha podido abrir el socket UDP para FINS.", e);
        } catch (IOException e) {
            throw new RuntimeException("No se ha podido resolver la IP del PLC: " + this.plcIp, e);
        }
    }

    public List<Maquina> decodificarCio(byte[] datosCio, List<ConfiguracionMaquinaPLC> configuraciones) {
        if (datosCio == null) {
            throw new IllegalArgumentException("Los datos CIO no pueden ser nulos.");
        }
        if (datosCio.length < TAMANO_CIO_BYTES) {
            throw new IllegalArgumentException("Se esperaban al menos " + TAMANO_CIO_BYTES + " bytes CIO.");
        }
        if (configuraciones == null) {
            throw new IllegalArgumentException("Las configuraciones de maquina no pueden ser nulas.");
        }

        List<Maquina> maquinas = new ArrayList<>(configuraciones.size());

        for (ConfiguracionMaquinaPLC configuracion : configuraciones) {
            boolean bitAutomatico = leerBit(datosCio, configuracion.offsetAuto, configuracion.bitAuto);
            boolean bitManual = leerBit(datosCio, configuracion.offsetManu, configuracion.bitManu);

            // El contador sale del bit inmediatamente anterior al bit de automatico.
            // OMRON guarda las palabras en big-endian, asi que primero localizamos la
            // palabra y despues desplazamos el bit hasta la posicion menos significativa.
            int offsetContador = configuracion.offsetAuto;
            int bitContador = configuracion.bitAuto - 1;
            if (bitContador < 0) {
                offsetContador = configuracion.offsetAuto - 1;
                bitContador = 15;
            }

            boolean bitContadorActivo = offsetContador >= 0 && leerBit(datosCio, offsetContador, bitContador);

            EstadoMaquina estado = resolverEstado(bitAutomatico, bitManual);
            int contadorBruto = bitContadorActivo ? 1 : 0;

            maquinas.add(new Maquina(configuracion.idMaquina, estado, contadorBruto, 0.0d));
        }

        return maquinas;
    }

    /**
     * Devuelve exactamente los 8 bytes de datos CIO contenidos en la respuesta FINS.
     */
    public byte[] leerDatosCioCrudos() {
        return leerMemoriaPalabras(cioMemoryAreaCode, cioStartWord, cioWordsToRead, TAMANO_CIO_BYTES);
    }

    private synchronized byte[] leerMemoriaPalabras(int memoryAreaCode, int startWord, int wordsToRead, int expectedDataBytes) {
        byte sid = (byte) (System.nanoTime() & 0xFF);
        byte[] request = construirPeticionMemoria(sid, memoryAreaCode, startWord, wordsToRead);

        try {
            DatagramPacket peticion = new DatagramPacket(request, request.length, ipPlc, plcPort);
            socket.send(peticion);

            byte[] buffer = new byte[Math.max(256, FINS_HEADER_BYTES + expectedDataBytes + 32)];
            DatagramPacket respuesta = new DatagramPacket(buffer, buffer.length);
            
            while (true) {
                socket.receive(respuesta);
                if (respuesta.getLength() >= 10 && respuesta.getData()[9] == sid) {
                    break;
                }
            }

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

    private static EstadoMaquina resolverEstado(boolean bitAutomatico, boolean bitManual) {
        if (bitAutomatico && !bitManual) {
            return EstadoMaquina.AUTOMATICO;
        }
        if (bitManual && !bitAutomatico) {
            return EstadoMaquina.MANUAL;
        }
        return EstadoMaquina.PARO;
    }

    /**
     * Bit-shifting sobre palabras OMRON de 16 bits:
     * 1) Cada palabra ocupa 2 bytes.
     * 2) La posicion real del bit se calcula como base + bit.
     * 3) Se reconstituye la palabra con big-endian: high << 8 | low.
     * 4) Se desplaza a la derecha hasta dejar el bit objetivo en la posicion 0.
     * 5) Se enmascara con 0x01 para obtener 0 o 1.
     */
    private static boolean leerBit(byte[] datosCio, int indicePalabra, int indiceBit) {
        if (indicePalabra < 0) {
            return false;
        }

        int base = indicePalabra * BYTES_POR_PALABRA;
        if (base + 1 >= datosCio.length) {
            return false;
        }

        int high = datosCio[base] & 0xFF;
        int low = datosCio[base + 1] & 0xFF;
        int palabra = (high << 8) | low;
        return ((palabra >> indiceBit) & 0x01) == 1;
    }

    @Override
    public void close() {
        if (!socket.isClosed()) {
            socket.close();
        }
    }
}
