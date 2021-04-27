package amosproj.server;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest // generates the main application context for testing
@TestPropertySource(locations = "classpath:test.properties")
class ServerApplicationTest {

    @Test
    public void contextLoads() {
    }

}
