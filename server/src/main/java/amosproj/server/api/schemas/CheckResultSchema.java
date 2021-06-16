package amosproj.server.api.schemas;

import amosproj.server.data.CheckResult;
import amosproj.server.data.CheckSeverity;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.utils.JacksonJson;

/**
 * Dies ist das Schema Objekt, welches von der API an das Frontend gesendet wird.
 * CheckResultSchema wird benutzt, um die CheckResults aus der Datenbank zu dekorieren mit Daten aus der config-Datei.
 * Damit wird Redundanz in der Datenbank umgangen.
 */
public class CheckResultSchema {
    // core attributes
    private String checkName;
    private Boolean result;
    // relations
    // -
    // additional info
    private CheckSeverity severity;
    private String description;
    private String message;
    private String fix;
    private String tag;
    private int priority;

    public CheckResultSchema(CheckResult result, JsonNode node) {
        this.checkName = result.getCheckName();
        this.result = result.getResult();
        if (node != null) {
            this.severity = CheckSeverity.valueOf(node.get("severity").asText());
            this.description = node.get("description").asText();
            this.message = node.get("message").asText();
            this.fix = node.get("fix").asText();
            this.tag = node.get("tag").asText();
            this.priority = node.get("priority").asInt();
        }
    }

    public String getCheckName() {
        return checkName;
    }

    public Boolean getResult() {
        return result;
    }

    public CheckSeverity getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    public String getMessage() {
        return message;
    }

    public String getFix() {
        return fix;
    }

    public String getTag() {
        return tag;
    }

    public int getPriority() { return priority; }

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
