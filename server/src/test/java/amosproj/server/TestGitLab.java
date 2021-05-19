package amosproj.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class TestGitLab {

    @Autowired
    private GitLab api;

    @Test
    void test_makeApiRequest() throws JsonProcessingException {
        String data = "{\n" +
                "  \"userId\": 1,\n" +
                "  \"id\": 1,\n" +
                "  \"title\": \"sunt aut facere repellat provident occaecati excepturi optio reprehenderit\",\n" +
                "  \"body\": \"quia et suscipit\\nsuscipit recusandae consequuntur expedita et cum\\nreprehenderit molestiae ut ut quas totam\\nnostrum rerum est autem sunt rem eveniet architecto\"\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode expected = mapper.readTree(data);
        assert expected != null;
        JsonNode actual = api.makeApiRequest("https://jsonplaceholder.typicode.com/posts/1");
        assert actual != null;

        assertEquals(expected, actual);
    }

}
