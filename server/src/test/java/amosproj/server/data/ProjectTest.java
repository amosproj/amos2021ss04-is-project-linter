package amosproj.server.data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureDataJpa
public class ProjectTest {

    @Autowired
    ProjectRepository projectRepository;

    @Test
    public void checkDataBaseIntegrity() {
        Project proj = new Project("herbstluft", "test", 1, "gitlab.com");
        projectRepository.save(proj);
        
    }

}
