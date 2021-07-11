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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String checkName;  // name of the test associated with java
    private Boolean result;
    // FK
    private Long lintId;

    protected CheckResult() {
    }


    public CheckResult(LintingResult lint, String checkName, Boolean result) {
        this.checkName = checkName;
        this.result = result;
        if (lint != null) this.lintId = lint.getId();
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

    public Boolean getResult() {
        return result;
    }



    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
