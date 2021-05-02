package amosproj.server.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureDataJpa
@TestPropertySource(locations = "classpath:test.properties")
public class LintingResultTest {

    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    LintingResultRepository lintingResultRepository;

    private Project testProj = null;

    @BeforeEach
    void setup() {
        testProj = new Project("ChiefExam", "https://gitlab.cs.fau.de/it62ajow/chiefexam", 1, "https://gitlab.cs.fau.de");
        projectRepository.save(testProj);
    }

    @AfterEach
    void tearDown() {
        lintingResultRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void checkResultTest() {
        LintingResult lintingResult = new LintingResult(testProj, LocalDateTime.now());
        lintingResultRepository.save(lintingResult);
        assertNotNull(lintingResultRepository.findAll());
    }
    
}
