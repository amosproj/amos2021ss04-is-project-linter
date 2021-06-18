package amosproj.server.data;

import org.gitlab4j.api.utils.JacksonJson;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Project ist das JPA-Objekt, das die Metadaten der einzelnen Repositories in der Datenbank speichert.
 */
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;
    private String name;
    private String url;
    private Integer gitlabProjectId;
    private String gitlabInstance;
    private String description;
    private Integer forkCount;
    private LocalDateTime lastCommit;

    @OneToMany(targetEntity = LintingResult.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private List<LintingResult> results;

    protected Project() {
    } // only for JPA, dont use directly!

    public Project(String name, String url, Integer gitlabProjectId, String gitlabInstance) { // Deprecated, still used for testing
        this.name = name;
        this.url = url;
        this.gitlabProjectId = gitlabProjectId;
        this.gitlabInstance = gitlabInstance;
    }

    public Project(String name, String url, Integer gitlabProjectId, String gitlabInstance, String description, Integer forkCount, LocalDateTime lastCommit) {
        this.name = name;
        this.url = url;
        this.gitlabProjectId = gitlabProjectId;
        this.gitlabInstance = gitlabInstance;
        this.description = description;
        this.forkCount = forkCount;
        this.lastCommit = lastCommit;
    }

    public Long getId() {
        return Id;
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

    public List<LintingResult> getResults() {
        return results;
    }

    public void setResults(List<LintingResult> results) {
        this.results = results;
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

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }
}
