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
public class CheckResultTest {

    @Autowired
    CheckResultRepository checkResultRepository;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    LintingResultRepository lintingResultRepository;

    private Project testProj = null;
    private LintingResult lintingResult = null;

    @BeforeEach
    void setup() {
        testProj = new Project("meme-repo", "https://gitlab.com/be15piel/meme-repo", 69, "gitlab.com", "", 0, LocalDateTime.now(Clock.systemUTC()));
        projectRepository.save(testProj);
        lintingResult = new LintingResult(testProj, LocalDateTime.now());
        lintingResultRepository.save(lintingResult);
    }

    @AfterEach
    void tearDown() {
        checkResultRepository.deleteAll();
        lintingResultRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void checkResultTest() {
        CheckResult checkresult = new CheckResult(lintingResult, "readme.md", true);
        assertNotNull(checkresult);
        checkResultRepository.save(checkresult);

        assertNotNull(checkResultRepository.findAll());
        Iterable<CheckResult> lintingResults = checkResultRepository.findAll();
        Iterator<CheckResult> it = lintingResults.iterator();
        while (it.hasNext()) {
            CheckResult checkResultIntegrity = it.next();
            assertNotNull(checkResultIntegrity);
            assertEquals(checkresult.getCheckName(), checkResultIntegrity.getCheckName());
            assertEquals(checkresult.getId(), checkResultIntegrity.getId());
            assertEquals(checkresult.getResult(), checkResultIntegrity.getResult());
        }
    }
}
