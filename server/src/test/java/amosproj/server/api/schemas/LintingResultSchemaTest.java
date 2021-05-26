package amosproj.server.api.schemas;

import amosproj.server.data.LintingResult;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureDataJpa
@TestPropertySource(locations = "classpath:test.properties")
public class LintingResultSchemaTest {

    @Test
    public void testLintingResult() {
        LintingResult lintingResult = new LintingResult(null, null);
        LintingResultSchema lintingResultSchema = new LintingResultSchema(lintingResult);

        // some assertions
        assertEquals(lintingResult.getLintTime(), lintingResultSchema.getLintTime());
    }

}
