package amosproj.server.api.schemas;

import amosproj.server.Config;
import amosproj.server.data.CheckResult;
import amosproj.server.data.CheckSeverity;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
// DataJPATest baut kein richtigen Spring Context auf, und liest somit nicht die ENV Variablen ein, die wir brauchen.
@TestPropertySource(locations = "classpath:test.properties")
public class CheckResultSchemaTest {

    @Test
    public void testCheckResultSchema() {
        JsonNode node = Config.getConfigNode().get("checks").get("CheckReadmeExistence");
        CheckResult checkResult = new CheckResult(null, "CheckReadmeExistence", true);
        CheckResultSchema checkResultSchema = new CheckResultSchema(checkResult, node);
        // some assertions
        assertEquals(checkResult.getResult(), checkResultSchema.getResult());
        assertEquals(checkResult.getCheckName(), checkResultSchema.getCheckName());

        assertEquals(node.get("description").asText(), checkResultSchema.getDescription());
        assertEquals(node.get("fix").asText(), checkResultSchema.getFix());
        assertEquals(node.get("tag").asText(), checkResultSchema.getTag());
        assertEquals(CheckSeverity.valueOf(node.get("severity").asText()), checkResultSchema.getSeverity());
        assertEquals(node.get("message").asText(), checkResultSchema.getMessage());
        assertEquals(node.get("priority").asInt(), checkResultSchema.getPriority());
    }
}
