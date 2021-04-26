package amosproj.server.linter;

import amosproj.server.data.LintingResult;
import amosproj.server.data.Project;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class Linter {

    @Value("${GITLAB_ACCESS_TOKEN}")
    private String apitoken;

    // entry point for api
    public LintingResult getResult(String repoUrl) {
        // get Objects
//        Project lintingProject = projectRepository.findByUrl(repoUrl);
        // start linting
        return checkEverything(new Project("herbstluft", repoUrl, 0, "gitlab.com"));
    }

    public LintingResult checkEverything(Project project) {
        String URL = project.getUrl();
        GitLabApi gitLabApi = new GitLabApi("https://gitlab.cs.fau.de", apitoken);
        gitLabApi.enableRequestResponseLogging();
        List<org.gitlab4j.api.models.Project> projects = null;
        try {
            projects = gitLabApi.getProjectApi().getProjects();
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
        System.out.println(projects);
        return null;
    }


}
