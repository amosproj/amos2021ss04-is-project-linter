package amosproj.server.api.schemas;

import amosproj.server.Config;
import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.utils.JacksonJson;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Dies ist das Schema Objekt, welches von der API an das Frontend gesendet wird.
 * LintingResultSchema wird benutzt, um die LintingResults aus der Datenbank zu dekorieren mit Daten aus der config-Datei.
 * Damit wird Redundanz in der Datenbank umgangen.
 */
public class LintingResultSchema {
    // core attributes
    private Long Id;
    private LocalDateTime lintTime;
    // relations
    private List<CheckResultSchema> checkResults;
    // additional info
    // -

    public LintingResultSchema(LintingResult lr) {
        BeanUtils.copyProperties(lr, this);
        this.checkResults = new LinkedList<>();
        JsonNode config = Config.getConfigNode();
        if (lr.getCheckResults() != null)
            for (CheckResult cr : lr.getCheckResults())
                this.checkResults.add(new CheckResultSchema(cr, config.get("checks").get(cr.getCheckName())));
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public LocalDateTime getLintTime() {
        return lintTime;
    }

    public void setLintTime(LocalDateTime lintTime) {
        this.lintTime = lintTime;
    }

    public List<CheckResultSchema> getCheckResults() {
        return checkResults;
    }

    public void setCheckResults(List<CheckResultSchema> checkResults) {
        this.checkResults = checkResults;
    }

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
