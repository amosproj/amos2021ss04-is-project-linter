package amosproj.server.data;

import org.gitlab4j.api.utils.JacksonJson;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * LintingResult ist das JPA-Objekt, das die Meta-Daten der Linterausführungen in der Datenbank speichert.
 */
@Entity
public class LintingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private Long projectId;
    private LocalDateTime lintTime;

    @OneToMany(targetEntity = CheckResult.class)
    @JoinColumn(name = "lintId")
    private List<CheckResult> checkResults;

    protected LintingResult() {
    }

    public LintingResult(Project project, LocalDateTime lintTime) {
        if (project != null) this.projectId = project.getId();
        this.lintTime = lintTime;
    }

    public Long getId() {
        return Id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public LocalDateTime getLintTime() {
        return lintTime;
    }

    public void setLintTime(LocalDateTime lintTime) {
        this.lintTime = lintTime;
    }

    public List<CheckResult> getCheckResults() {
        return checkResults;
    }

    public void setCheckResults(List<CheckResult> checkResults) {
        this.checkResults = checkResults;
    }

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
