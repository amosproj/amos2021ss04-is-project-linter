package amosproj.server.data;

import org.gitlab4j.api.utils.JacksonJson;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * CheckResult ist das JPA-Objekt, das die Ergebnisse der einzelnen Checks in der Datenbank speichert.
 */
@Entity
public class CheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String checkName;  // name of the test associated with java
    private Boolean result;
    private CheckSeverity severity;
    // FK
    private Long lintId;

    protected CheckResult() {
    }


    public CheckResult(LintingResult lint, String checkName, Boolean result, CheckSeverity severity) {
        this.checkName = checkName;
        this.result = result;
        this.severity = severity;
        this.lintId = lint.getId();
    }

    public Long getId() {
        return Id;
    }

    public Long getLintId() {
        return lintId;
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

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
