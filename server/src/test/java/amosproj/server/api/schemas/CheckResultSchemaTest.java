package amosproj.server.api.schemas;

import amosproj.server.data.CheckResult;
import amosproj.server.linter.Linter;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureDataJpa
@TestPropertySource(locations = "classpath:test.properties")
public class CheckResultSchemaTest {

    @Test
    public void testCheckResultSchema() {
        JsonNode node = Linter.getConfigNode().get("checks").get("checkReadmeExistence");
        CheckResult checkResult = new CheckResult(null, "checkReadmeExistence", true);
        CheckResultSchema checkResultSchema = new CheckResultSchema(checkResult, node);
        // some assertions
        assertEquals(checkResult.getResult(), checkResultSchema.getResult());
        assertEquals(checkResult.getCheckName(), checkResultSchema.getCheckName());

        assertEquals(checkResultSchema.getDescription(), node.get("description").asText());
        assertEquals(checkResultSchema.getFix(), node.get("fix").asText());
    }
}
