package amosproj.server.linter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class CrawlerTest {

    @Autowired
    private Crawler crawler;

    @Test
    public void testRunCrawler() {
        assertFalse(crawler.getCrawlerActive());
        //crawler.runCrawler();
    }

}
