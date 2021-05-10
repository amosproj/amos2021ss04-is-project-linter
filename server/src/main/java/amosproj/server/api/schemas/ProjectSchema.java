package amosproj.server.api.schemas;

import amosproj.server.GitLab;
import amosproj.server.data.Project;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.utils.JacksonJson;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

public class ProjectSchema {
    private String description;
    private Date lastCommit;
    private String name;
    private String url;
    private Long ID;
    private List<LintingResultSchema> results;

    public ProjectSchema(Project proj, GitLab api, boolean withResults) {
        if (!withResults) {
            proj.setResults(null);
        }
        this.setID(proj.getId());
        BeanUtils.copyProperties(proj, this);
//        try {
//            org.gitlab4j.api.models.Project gitlabProj = api.getApi().getProjectApi().getProject(proj.getUrl());
//            description = gitlabProj.getDescription();
//            lastCommit = gitlabProj.getLastActivityAt();
//        } catch (GitLabApiException e) {
//            e.printStackTrace();
//        }
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
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

    public List<LintingResultSchema> getResults() {
        return results;
    }

    public void setResults(List<LintingResultSchema> results) {
        this.results = results;
    }

    public String toString() {
        return JacksonJson.toJsonString(this);
    }

}
