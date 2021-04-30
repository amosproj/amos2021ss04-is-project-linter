package amosproj.server.data;

import org.gitlab4j.api.utils.JacksonJson;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class CheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String checkName;  // name of the test asssociated with java
    private Boolean result;
    // FK
    private Long lintId;

    protected CheckResult() {
    }

    public CheckResult(LintingResult lintingResult, String checkName, Boolean result) {
        this.lintId = lintingResult.getId();
        this.checkName = checkName;
        this.result = result;
    }

    public Long getId() {
        return id;
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

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
