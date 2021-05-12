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
        String data = "{\"status\":\"success\",\"data\":{\"id\":1,\"employee_name\":\"Tiger Nixon\",\"employee_salary\":320800,\"employee_age\":61,\"profile_image\":\"\"},\"message\":\"Successfully! Record has been fetched.\"}";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode expected = mapper.readTree(data);
        assert expected != null;
        JsonNode actual = api.makeApiRequest("http://dummy.restapiexample.com/api/v1/employee/1");
        assert actual != null;

        assertEquals(expected, actual);
    }

}
