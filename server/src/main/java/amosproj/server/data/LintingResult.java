package amosproj.server.data;

import org.gitlab4j.api.utils.JacksonJson;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class LintingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long projectId;
    private LocalDateTime lintTime;

    @OneToMany(targetEntity = FileCheck.class)
    @JoinColumn(name = "lintId")
    private List<FileCheck> fileChecks;

    @OneToOne(targetEntity = SettingsCheck.class)
    @JoinColumn(name = "id")
    private SettingsCheck settingsCheck;

    public LintingResult(Project project, LocalDateTime lintTime) {
        this.projectId = project.getId();
        this.lintTime = lintTime;
    }

    protected LintingResult() {
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getLintTime() {
        return lintTime;
    }

    public void setLintTime(LocalDateTime lintTime) {
        this.lintTime = lintTime;
    }

    public List<FileCheck> getFileChecks() {
        return fileChecks;
    }

    public void setFileChecks(List<FileCheck> fileChecks) {
        this.fileChecks = fileChecks;
    }

    public SettingsCheck getSettingsCheck() {
        return settingsCheck;
    }

    public void setSettingsCheck(SettingsCheck settingsCheck) {
        this.settingsCheck = settingsCheck;
    }

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
