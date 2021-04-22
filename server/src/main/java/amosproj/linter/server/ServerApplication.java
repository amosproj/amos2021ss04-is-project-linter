package amosproj.linter.server;

import amosproj.linter.server.data.Project;
import amosproj.linter.server.data.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServerApplication {

    private static final Logger log = LoggerFactory.getLogger(ServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(ProjectRepository repository) {
        return (args) -> {
            // save a few customers
            repository.save(new Project("Herbsluftwm", "https://gitlab.com/altaway/herbstluftwm"));
            // fetch all customers
            log.info("Customers found with findAll():");
            for (Project proj : repository.findAll()) {
                log.info(proj.toString());
            }
            // fetch an individual customer by ID
            Project proj = repository.findById(1L);
            log.info("Customer found with findById(1L):");
            log.info(proj.toString());
        };
    }

}
