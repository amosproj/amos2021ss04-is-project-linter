package amosproj.server.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class LintingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId;
    private LocalDateTime lintTime;

    public LintingResult(Project project, LocalDateTime lintTime) {
        this.projectId = project.getId();
        this.lintTime = lintTime;
    }

    protected LintingResult() {    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getLintTime() {
        return lintTime;
    }

    public void setLintTime(LocalDateTime lintTime) {
        this.lintTime = lintTime;
    }
}
