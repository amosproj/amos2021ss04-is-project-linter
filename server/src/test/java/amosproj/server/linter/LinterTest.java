package amosproj.server.linter;

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
    public void testRunCrawler() {
        assert crawler.startCrawler();
    }

}
