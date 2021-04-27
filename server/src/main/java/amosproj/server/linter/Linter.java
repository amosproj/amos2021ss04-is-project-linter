package amosproj.server.linter;

import amosproj.server.GitLab;
import amosproj.server.data.*;
import amosproj.server.linter.checks.CheckGitlabFiles;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class Linter {

    @Autowired
    private GitLab api;

    @Autowired
    private LintingResultRepository lintingResultRepository;
    @Autowired
    private FileCheckRepository fileCheckRepository;
    @Autowired
    private ProjectRepository projectRepository;


    public void runLint(String repoUrl) throws GitLabApiException {
        String path = repoUrl.replace(api.getGitlabHost() + "/", "");

        org.gitlab4j.api.models.Project proj = api.getApi().getProjectApi().getProject(path);
        assert proj != null;
        checkEverything(proj);
    }

    private void checkEverything(org.gitlab4j.api.models.Project apiProject) {
        // Hole LintingProject
        Project currLintingProject = projectRepository.findByProjectId(apiProject.getId());
        if (currLintingProject == null) {
            currLintingProject = new Project(apiProject.getName(), apiProject.getWebUrl(), apiProject.getId(), api.getGitlabHost());
            projectRepository.save(currLintingProject);
        }
        // Erstelle neues Linting Result
        LintingResult res = new LintingResult(currLintingProject, LocalDateTime.now());
        // Fuehre Checks aus
        var filesChecker = new CheckGitlabFiles(api.getApi(), apiProject);
        // speichere ergebnis
        lintingResultRepository.save(res);
        FileCheck fileCheck = new FileCheck(res, "readme.md", filesChecker.fileExists("readme.md"));
        fileCheckRepository.save(fileCheck);
    }

    @Scheduled(cron = "0 0 * * * ?") // every 24 hours at midnight
    public void runCrawler() {
        List<org.gitlab4j.api.models.Project> projects = null;
        try {
            projects = api.getApi().getProjectApi().getProjects();
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }

        for (org.gitlab4j.api.models.Project proj : projects) {
            checkEverything(proj);
        }

    }

}
