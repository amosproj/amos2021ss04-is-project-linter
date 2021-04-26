package amosproj.server.linter;

import amosproj.server.data.LintingResult;
import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import amosproj.server.data.SettingsCheckRepository;
import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class Linter {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    SettingsCheckRepository settingsCheckRepository;

    // entry point for api
    public LintingResult getResult(String repoUrl) {
        // get Objects
//        Project lintingProject = projectRepository.findByUrl(repoUrl);
        // start linting
        return checkEverything(new Project("herbstluft",  repoUrl, 0, "gitlab.com"));
    }

    public LintingResult checkEverything(Project project) {
        String URL = project.getUrl();
//        GitLabApi gitLabApi = new GitLabApi("http://gitlab.cs.fau.de", "hTYRDByrXsDHxj_MPVWU");
//        gitLabApi.enableRequestResponseLogging();
//        List<Project> projects = gitLabApi.getProjectApi().getProjects();
        return null;
    }


}
