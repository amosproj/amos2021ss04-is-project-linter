package main.java.amosproj.linter.server.data;

import main.java.amosproj.linter.server.data.Project;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class LintingResults {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId;
    private LocalDateTime lintTime;

    public LintingResults(Project project, LocalDateTime lintTime) {
        this.projectId = project.getId();
        this.lintTime = lintTime;
    }

    protected LintingResults() {    }

    @Override
    public String toString() {
        return "LintingResults{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", lintTime=" + lintTime +
                '}';
    }
    public Long getId() {
        return id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setLastLint(LocalDateTime lastLint) {
        this.lastLint = lastLint;
    }

    public LocalDateTime getLastLint() {
        return lastLint;
    }

}
