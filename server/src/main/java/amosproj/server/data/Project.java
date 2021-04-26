package amosproj.server.data;

import javax.persistence.*;
import java.util.List;

@Entity
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String url;
    private Integer projectId;
    private String gitlabInstance;

    @OneToMany(targetEntity = LintingResult.class)
    private List<LintingResult> results;

    protected Project() {
    } // only for JPA, dont use directly!

    public Project(String name, String url, Integer projectId, String gitlabInstance) {
        this.name = name;
        this.url = url;
        this.projectId = projectId;
        this.gitlabInstance = gitlabInstance;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public String getGitlabInstance() {
        return gitlabInstance;
    }

    public List<LintingResult> getResults() {
        return results;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", projectId=" + projectId +
                ", gitlabInstance='" + gitlabInstance + '\'' +
                ", results=" + results +
                '}';
    }



}
