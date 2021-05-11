package amosproj.server.api.schemas;

import amosproj.server.GitLab;
import amosproj.server.data.LintingResult;
import amosproj.server.data.Project;
import org.gitlab4j.api.utils.JacksonJson;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ProjectSchema {
    // core attributes
    private Long Id;
    private String name;
    private String url;
    private Integer gitlabProjectId;
    private String gitlabInstance;
    // relations
    private List<LintingResultSchema> lintingResults;
    // extra info
    private String description;
    private Date lastCommit;

    public ProjectSchema(Project proj, GitLab api, boolean withResults) {
        if (!withResults) {
            proj.setResults(null);
        }

        System.out.println(proj.toString());

        BeanUtils.copyProperties(proj, this);
        this.lintingResults = new LinkedList<>();

        if (proj.getResults() != null)
            for (LintingResult lr : proj.getResults())
                this.lintingResults.add(new LintingResultSchema(lr));

        /*try {
            org.gitlab4j.api.models.Project gitlabProj = api.getApi().getProjectApi().getProject(proj.getUrl());
            description = gitlabProj.getDescription();
            lastCommit = gitlabProj.getLastActivityAt();
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }*/
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

    public List<LintingResultSchema> getLintingResults() {
        return lintingResults;
    }

    public void setLintingResults(List<LintingResultSchema> lintingResults) {
        this.lintingResults = lintingResults;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(Date lastCommit) {
        this.lastCommit = lastCommit;
    }

    @Override
    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
