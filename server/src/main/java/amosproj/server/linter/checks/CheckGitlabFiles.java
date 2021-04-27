package amosproj.server.linter.checks;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;

public class CheckGitlabFiles {

    private GitLabApi api;
    private org.gitlab4j.api.models.Project proj;

    public CheckGitlabFiles(GitLabApi api, org.gitlab4j.api.models.Project project) {
        this.api = api;
        this.proj = project;
    }

    public boolean fileExists(String filepath) {
        try {
            RepositoryFile file = api.getRepositoryFileApi().getFileInfo(proj.getId(), filepath, "master");
        } catch (GitLabApiException e) {
            return false;
        }
        return true;
    }

}
