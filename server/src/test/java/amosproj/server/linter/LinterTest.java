package amosproj.server.linter;

import amosproj.server.TestUtil;
import org.gitlab4j.api.GitLabApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class LinterTest {

    @Autowired
    private Linter linter;

    @Autowired
    private Crawler crawler;

    @Test
    public void testRunLint() throws GitLabApiException {
        for (String repo : TestUtil.getTestRepos()) {
            linter.runLint(repo);
        }
    }

    @Test
    public void testRunCrawler() {
        assert crawler.startCrawler();
    }

}
