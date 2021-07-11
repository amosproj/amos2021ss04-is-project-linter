package amosproj.server.data;

import org.gitlab4j.api.utils.JacksonJson;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Project ist das JPA-Objekt, das die Metadaten der einzelnen Repositories in der Datenbank speichert.
 */
@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String name;
    private String nameSpace;
    private String url;
    private Integer gitlabProjectId;
    private String description;
    private Integer forkCount;
    private LocalDateTime lastCommit;

    @OneToMany(targetEntity = LintingResult.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "projectId")
    private List<LintingResult> results;

    protected Project() {
    } // only for JPA, dont use directly!


    public Project(String name, String url, Integer gitlabProjectId, String nameSpace, String description, Integer forkCount, LocalDateTime lastCommit) {
        this.name = name;
        this.nameSpace = nameSpace;
        this.url = url;
        this.gitlabProjectId = gitlabProjectId;
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

    public Integer getGitlabProjectId() {
        return gitlabProjectId;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public List<LintingResult> getResults() {
        return results;
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
