package amosproj.server.api.schemas;

import amosproj.server.data.LintingResult;
import amosproj.server.data.Project;
import org.gitlab4j.api.utils.JacksonJson;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Dies ist das Schema Objekt, welches von der API an das Frontend gesendet wird.
 * ProjectSchema kann benutzt werden, um die Projects aus der Datenbank zu dekorieren mit Daten aus der config-Datei.
 * Damit kann Redundanz in der Datenbank umgangen werden.
 */
public class ProjectSchema {
    // core attributes
    private Long Id;
    private String name;
    private String url;
    private Integer gitlabProjectId;
    private String gitlabInstance;
    private String description;
    private Integer forkCount;
    private LocalDateTime lastCommit;
    // relations
    private List<LintingResultSchema> lintingResults;
    // extra info

    public ProjectSchema(Project proj, List<LintingResult> lintingResults) {
        BeanUtils.copyProperties(proj, this);
        this.lintingResults = new LinkedList<>();
        for (LintingResult lr : lintingResults) {
            this.lintingResults.add(new LintingResultSchema(lr));
        }
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getGitlabProjectId() {
        return gitlabProjectId;
    }

    public void setGitlabProjectId(Integer gitlabProjectId) {
        this.gitlabProjectId = gitlabProjectId;
    }

    public String getGitlabInstance() {
        return gitlabInstance;
    }

    public void setGitlabInstance(String gitlabInstance) {
        this.gitlabInstance = gitlabInstance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getForkCount() {
        return forkCount;
    }

    public void setForkCount(Integer forkCount) {
        this.forkCount = forkCount;
    }

    public LocalDateTime getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(LocalDateTime lastCommit) {
        this.lastCommit = lastCommit;
    }

    public List<LintingResultSchema> getLintingResults() {
        return lintingResults;
    }

    public void setLintingResults(List<LintingResultSchema> lintingResults) {
        this.lintingResults = lintingResults;
    }

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
