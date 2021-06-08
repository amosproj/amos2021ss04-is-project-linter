package amosproj.server.linter;

import amosproj.server.Config;
import amosproj.server.GitLab;
import amosproj.server.data.*;
import amosproj.server.linter.checks.CheckGitlabFiles;
import amosproj.server.linter.checks.CheckGitlabRoles;
import amosproj.server.linter.checks.CheckGitlabSettings;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Iterator;

/**
 * Linter führt den tatsächlichen Lint-Vorgang durch.
 * Weiterhin stellt sie eine Methode zur Verfügung, um die config-Datei einzulesen.
 */
@Service
public class Linter {

    @Autowired
    private GitLab api;
    @Autowired
    private LintingResultRepository lintingResultRepository;
    @Autowired
    private CheckResultRepository checkResultRepository;
    @Autowired
    private ProjectRepository projectRepository;

    /**
     * entry point to start asynchronous linting process
     *
     * @param repoUrl
     * @throws GitLabApiException
     */
    public void runLint(String repoUrl) throws GitLabApiException {
        repoUrl = repoUrl.replace("\r", "");
        String path = repoUrl.replace(api.getGitlabHost() + "/", "");

        org.gitlab4j.api.models.Project proj = api.getApi().getProjectApi().getProject(path);
        assert proj != null;
        checkEverything(proj, LocalDateTime.now());
    }

    /**
     * internal method that starts all checks, and saves the results
     *
     * @param apiProject
     */
    public void checkEverything(org.gitlab4j.api.models.Project apiProject, LocalDateTime timestamp) {
        // Hole LintingProject
        Project currLintingProject = projectRepository.findFirstByGitlabProjectId(apiProject.getId());
        if (currLintingProject == null) {
            // Erstelle neues Projekt mit Description und ForkCount
            currLintingProject = new Project(apiProject.getName(), apiProject.getWebUrl(), apiProject.getId(), api.getGitlabHost(), apiProject.getDescription(), apiProject.getForksCount(), apiProject.getLastActivityAt());
            projectRepository.save(currLintingProject);
        } else {
            // Update Description, LastActivity und ForkCount
            currLintingProject.setDescription(apiProject.getDescription());
            currLintingProject.setForkCount(apiProject.getForksCount());
            currLintingProject.setLastCommit(apiProject.getLastActivityAt());
        }

        // Erstelle neues Linting Result
        LintingResult lintingResult = new LintingResult(currLintingProject, timestamp);
        lintingResultRepository.save(lintingResult);

        // Fuehre Checks aus
        JsonNode checks = Config.getConfigNode().get("checks");

        var fileChecks = new CheckGitlabFiles(api, apiProject, lintingResult, checkResultRepository);
        var settingsChecks = new CheckGitlabSettings(api, apiProject, lintingResult, checkResultRepository);
        var roleChecks = new CheckGitlabRoles(api, apiProject, lintingResult, checkResultRepository);

        for (Iterator<String> it = checks.fieldNames(); it.hasNext(); ) {
            String testName = it.next();
            JsonNode check = checks.get(testName);
            switch (check.get("category").asText()) {
                case "file_checks":
                    fileChecks.runTest(testName, check);
                    break;
                case "settings_checks":
                    settingsChecks.runTest(testName, check);
                    break;
                case "role_checks":
                    roleChecks.runTest(testName, check);
                    break;
            }
        }

    }

}
