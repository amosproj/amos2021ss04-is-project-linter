package amosproj.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServerApplication {

    // Dies ist der Einstiegspunkt des Programms.
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

}
