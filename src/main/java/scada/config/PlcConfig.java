package scada.config;

import java.net.SocketException;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Profile;

import plc.ClienteFinsOmron;

@Configuration
@Profile("!portable")
@PropertySource("classpath:plc.properties")
public class PlcConfig {

    @Value("${plc.ip}")
    private String plcIp;

    @Value("${plc.port}")
    private int plcPort;

    @Value("${plc.node}")
    private int plcNode;

    @Value("${plc.timeoutMs}")
    private int timeoutMs;

    @Value("${plc.cio.memoryAreaHex}")
    private String cioMemoryAreaHex;

    @Value("${plc.cio.startWord}")
    private int cioStartWord;

    @Value("${plc.cio.wordsToRead}")
    private int cioWordsToRead;

    @Bean
    public ClienteFinsOmron clienteFinsOmron() throws SocketException, UnknownHostException {
        int memoryAreaCode = Integer.parseInt(cioMemoryAreaHex, 16);
        return new ClienteFinsOmron(plcIp, plcPort, plcNode, timeoutMs, memoryAreaCode, cioStartWord, cioWordsToRead);
    }

    @Bean
    public servicio.ConfiguracionFinsPlcServicio configuracionFinsPlcServicio() {
        return new servicio.ConfiguracionFinsPlcServicio();
    }
}
