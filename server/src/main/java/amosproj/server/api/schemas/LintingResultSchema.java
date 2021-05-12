package amosproj.server.api.schemas;

import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import org.gitlab4j.api.utils.JacksonJson;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
        if (lr.getCheckResults() != null)
            for (CheckResult cr : lr.getCheckResults())
                this.checkResults.add(new CheckResultSchema(cr));
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
