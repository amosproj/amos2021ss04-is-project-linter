package amosproj.server.data;

import org.gitlab4j.api.utils.JacksonJson;

import javax.persistence.*;
import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String name;
    private String url;
    private Integer gitlabProjectId;
    private String gitlabInstance;

    @OneToMany(targetEntity = LintingResult.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "projectId")
    private List<LintingResult> results;

    protected Project() {
    } // only for JPA, dont use directly!

    public Project(String name, String url, Integer gitlabProjectId, String gitlabInstance) {
        this.name = name;
        this.url = url;
        this.gitlabProjectId = gitlabProjectId;
        this.gitlabInstance = gitlabInstance;
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

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }


}
