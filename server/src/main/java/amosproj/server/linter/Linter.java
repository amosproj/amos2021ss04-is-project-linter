package amosproj.server.linter;

import amosproj.server.GitLab;
import amosproj.server.data.LintingResult;
import amosproj.server.data.LintingResultRepository;
import amosproj.server.data.Project;
import amosproj.server.data.SettingsCheck;
import amosproj.server.linter.checks.CheckGitlabSettings;
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


    public void runLint(String repoUrl) throws GitLabApiException {
        String path = repoUrl.replace(api.getGitlabHost() + "/", "");

        org.gitlab4j.api.models.Project proj = api.getApi().getProjectApi().getProject(path);
        assert proj != null;
        checkEverything(proj);
    }

    private void checkEverything(org.gitlab4j.api.models.Project apiProject) {
        Project currInternalProject = new Project(apiProject.getName(), apiProject.getWebUrl(), apiProject.getId(), api.getGitlabHost());
        // Erstelle neues Linting Result
        LintingResult res = new LintingResult(currInternalProject, LocalDateTime.now());

        // Führe tests aus:

        // Überprüfe die Einstellungen innerhalb des Repositorys
        CheckGitlabSettings checkSettings = new CheckGitlabSettings(apiProject);
        SettingsCheck setCheck = new SettingsCheck(res, checkSettings.isPublic(), checkSettings.hasRequestAccess(), checkSettings.usesGuestRole(),
                checkSettings.usesDeveloperRole(), checkSettings.usesGitLabPages());
        // Überprüfe nach Readme.md
        // Überprüfe nach Maintainers.md

        // speichere ergebnis
        lintingResultRepository.save(res);
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
