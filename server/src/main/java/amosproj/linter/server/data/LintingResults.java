package amosproj.linter.server.data;

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
    private LocalDateTime lastLint;
    private Long projectId;


    public LintingResults(LocalDateTime lastLint, Project project) {
        this.lastLint = lastLint;
        this.projectId = project.getId();
    }

    protected LintingResults() {    }

    @Override
    public String toString() {
        return "LintingResults{" +
                "id=" + id +
                ", lastLint=" + lastLint +
                ", projectId=" + projectId +
                '}';
    }

    public void setLastLint(LocalDateTime lastLint) {
        this.lastLint = lastLint;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getLastLint() {
        return lastLint;
    }

    public Long getProjectId() {
        return projectId;
    }

}
