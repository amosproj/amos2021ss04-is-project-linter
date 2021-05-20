package amosproj.server.api.schemas;

import amosproj.server.data.CheckResult;
import amosproj.server.data.CheckSeverity;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.utils.JacksonJson;
import org.springframework.beans.BeanUtils;

/**
 * CheckResultSchema wird benutzt, um die CheckResults aus der Datenbank zu dekorieren mit Daten aus der config-Datei.
 * Damit wird Redundanz in der Datenbank umgangen.
 */
public class CheckResultSchema {
    // core attributes
    private String checkName;
    private Boolean result;
    private CheckSeverity severity;
    private String category;
    // relations
    // -
    // additional info
    private String description;
    private String message;
    private String fix;

    public CheckResultSchema(CheckResult result, JsonNode node) {
        this.checkName = result.getCheckName();
        this.result = result.getResult();
        this.severity = result.getSeverity();
        this.category = node.get("category").asText();
        this.description = node.get("description").asText();
        this.message = node.get("message").asText();
        this.fix = node.get("fix").asText();
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public CheckSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(CheckSeverity severity) {
        this.severity = severity;
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

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
