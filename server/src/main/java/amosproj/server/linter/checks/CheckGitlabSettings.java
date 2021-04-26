package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Visibility;
import org.springframework.beans.factory.annotation.Autowired;

public class CheckGitlabSettings {

    @Autowired
    private GitLab api;

    public boolean isPublic(String apiUrl) {
        // call api
        org.gitlab4j.api.models.Project project = null;
        try {
            project = api.getApi().getProjectApi().getProject(apiUrl);
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }

        assert project != null;
        return project.getVisibility() == Visibility.PUBLIC;
    }
}
