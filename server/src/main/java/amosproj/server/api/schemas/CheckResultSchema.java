package amosproj.server.api.schemas;

import amosproj.server.data.CheckResult;
import amosproj.server.data.CheckSeverity;
import org.gitlab4j.api.utils.JacksonJson;
import org.springframework.beans.BeanUtils;

public class CheckResultSchema {
    // core attributes
    private Long Id;
    private String checkName;
    private Boolean result;
    private CheckSeverity severity;
    // relations
    // -
    // additional info
    // -

    public CheckResultSchema(CheckResult cr) {
        BeanUtils.copyProperties(cr, this);
        System.out.println(cr);
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
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
