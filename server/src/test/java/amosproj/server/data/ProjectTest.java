package amosproj.server.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@AutoConfigureDataJpa
@TestPropertySource(locations = "classpath:test.properties")
public class ProjectTest {

    @Autowired
    ProjectRepository projectRepository;

    private Project testProj = null;

    @BeforeEach
    void setUp() {
        testProj = new Project("ChiefExam", "https://gitlab.cs.fau.de/it62ajow/chiefexam", 1, "https://gitlab.cs.fau.de");
        projectRepository.save(testProj);
    }

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
    }

    @Test
    public void checkDataBaseIntegrity() {
        assert testProj != null;
        assert projectRepository.findByGitlabProjectId(1) != null;
    }

}
