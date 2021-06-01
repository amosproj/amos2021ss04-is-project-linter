package amosproj.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpStatusCodeException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class TestGitLab {

    @Autowired
    private GitLab api;

    @Test
    void test_makeApiRequest() throws JsonProcessingException {
        try {
            api.makeApiRequest("/projects/56456456456456");
        } catch (HttpStatusCodeException e) {
            assertEquals(e.getStatusCode().value(), 404);
        }
    }

}
