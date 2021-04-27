package amosproj.server.linter;

import amosproj.server.GitLab;
import amosproj.server.data.LintingResult;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Linter {

    @Autowired
    private GitLab api;

    // entry point for api
    public LintingResult getResult(String repoUrl) {
        org.gitlab4j.api.models.Project proj = null;
        try {
            proj = api.getApi().getProjectApi().getProject(repoUrl);
            System.out.println(proj);
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
        return checkEverything(proj);
    }

    public LintingResult checkEverything(org.gitlab4j.api.models.Project project) {
        System.out.println(project);
        //String URL = project.getUrl();

        return null;
    }

}
