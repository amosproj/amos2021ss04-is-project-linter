package amosproj.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Die Hauptklasse, mit welcher der gesamte Server von SpringBoot gestartet wird.
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
public class ServerApplication {

    /**
     * Dies ist der Einstiegspunkt des Programms.
     *
     * @param args Programmargumente
     */
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
