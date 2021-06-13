package amosproj.server;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

public class TestUtil {

    public static JsonNode getTestConfig() {
        Resource resource = new ClassPathResource("test.json");
        try {
            InputStream fileStream = resource.getInputStream();
            ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
            return objectMapper.readTree(fileStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
