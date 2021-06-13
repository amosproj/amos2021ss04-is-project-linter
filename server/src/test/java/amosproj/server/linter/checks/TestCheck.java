package amosproj.server.linter.checks;

import amosproj.server.TestUtil;
import amosproj.server.data.CheckResultRepository;
import amosproj.server.linter.Linter;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApiException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class TestCheck {

    @Autowired
    private Linter linter;

    @Autowired
    private CheckResultRepository checkResultRepository;

    @Test
    public void testAllPositive() throws GitLabApiException {
        JsonNode config = TestUtil.getTestConfig().get("https://gitlab.cs.fau.de/ib49uquh/allcheckstrue");
        linter.runLint("https://gitlab.cs.fau.de/ib49uquh/allcheckstrue");
        // compare results now
        for (Iterator<String> it = config.get("results").fieldNames(); it.hasNext(); ) {
            String checkName = it.next();
            boolean actual = checkResultRepository.findFirstByCheckName(checkName).getResult();
            boolean expected = config.get("results").get(checkName).asBoolean();
            assertEquals(expected, actual, checkName);
        }
    }

    @Test
    public void testAllNegative() throws GitLabApiException {
        JsonNode config = TestUtil.getTestConfig().get("https://gitlab.cs.fau.de/ib49uquh/allchecksfalse");
        linter.runLint("https://gitlab.cs.fau.de/ib49uquh/allchecksfalse");
        // compare results now
        for (Iterator<String> it = config.get("results").fieldNames(); it.hasNext(); ) {
            String checkName = it.next();
            boolean actual = checkResultRepository.findFirstByCheckName(checkName).getResult();
            boolean expected = config.get("results").get(checkName).asBoolean();
            assertEquals(expected, actual, checkName);
        }
    }

    @AfterEach
    void cleanDb() {
        checkResultRepository.deleteAll();
    }

}
