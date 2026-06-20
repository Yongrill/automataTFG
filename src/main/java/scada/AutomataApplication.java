package scada;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"scada", "servicio", "repositorio", "dominio", "utils", "plc", "produccion"})
@EnableJpaRepositories(basePackages = {"repositorio", "scada", "produccion"})
@EntityScan(basePackages = {"scada", "dominio", "repositorio", "produccion"})
@EnableScheduling
public class AutomataApplication {
    public static void main(String[] args) {
        SpringApplication.run(AutomataApplication.class, args);
    }
}
