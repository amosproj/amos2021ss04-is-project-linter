package amosproj.server.data;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        testProj = new Project("meme-repo", "https://gitlab.com/be15piel/meme-repo", 69, "gitlab.com", "", 0, LocalDateTime.now(Clock.systemUTC()));
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
        Iterable<LintingResult> lintingResults = lintingResultRepository.findAll();
        Iterator<LintingResult> it = lintingResults.iterator();
        while (it.hasNext()) {
            LintingResult lintingResult1 = it.next();
            assertNotNull(lintingResult1);
            assertNotNull(lintingResult1.getLintTime());
            assertNotNull(lintingResult1.getId());
            assertEquals(testProj.getId(), lintingResult1.getProjectId());
        }
    }

}
