package amosproj.server.linter;

import amosproj.server.GitLab;
import amosproj.server.data.LintingResult;
import amosproj.server.data.Project;
import amosproj.server.linter.checks.CheckGitlabSettings;
import amosproj.server.data.SettingsCheck;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
public class Linter {

    @Autowired
    private GitLab api;

    // entry point for api
    public LintingResult getOrCreateResult(String repoUrl) {
        org.gitlab4j.api.models.Project proj = null;
        try {
            proj = api.getApi().getProjectApi().getProject(repoUrl);
        } catch (GitLabApiException e) {
            //e.printStackTrace();
            return null; // URL was not valid
        }
        return checkEverything(proj);
    }

    public LintingResult checkEverything(org.gitlab4j.api.models.Project apiProject) {
        Project currInternalProject = new Project(apiProject.getName(), apiProject.getWebUrl(), apiProject.getId(),"");
        // Erstelle Linting Result
        LintingResult res = new LintingResult(currInternalProject, LocalDateTime.now());
        // Überprüfe die Einstellungen innerhalb des Repositorys
        CheckGitlabSettings checkSettings = new CheckGitlabSettings(apiProject);
        SettingsCheck setCheck = new SettingsCheck(res, checkSettings.isPublic(), checkSettings.hasRequestAccess(), checkSettings.usesGuestRole(),
                checkSettings.usesDeveloperRole(), checkSettings.usesGitLabPages());
        // Überprüfe nach Readme.md
        // Überprüfe nach Maintainers.md
        return res;
    }

}
