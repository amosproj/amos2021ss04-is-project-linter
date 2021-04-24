package amosproj.server.data;

import javax.persistence.*;
import java.io.File;
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
    private List fileChecks;

    @OneToOne(targetEntity = SettingsCheck.class)
    private SettingsCheck settingsCheck;

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

    public List getFileChecks() {
        return fileChecks;
    }

    public void setFileChecks(List fileChecks) {
        this.fileChecks = fileChecks;
    }

    public SettingsCheck getSettingsCheck() {
        return settingsCheck;
    }

    public void setSettingsCheck(SettingsCheck settingsCheck) {
        this.settingsCheck = settingsCheck;
    }
}
