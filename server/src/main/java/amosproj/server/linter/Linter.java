package amosproj.server.linter;

import amosproj.server.Config;
import amosproj.server.GitLab;
import amosproj.server.data.*;
import amosproj.server.linter.checks.Check;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
        // get project
        repoUrl = repoUrl.replace("\r", "");
        String path = repoUrl.replace(api.getGitlabHost() + "/", "");
        org.gitlab4j.api.models.Project proj = api.getApi().getProjectApi().getProject(path);
        // start linting process
        assert proj != null;
        checkEverything(proj, LocalDateTime.now(Clock.systemUTC()));
    }

    /**
     * internal method that starts all checks, and saves the results
     *
     * @param apiProject the project to lint
     * @param timestamp  under which timestamp the linting process should be saved
     */
    public void checkEverything(org.gitlab4j.api.models.Project apiProject, LocalDateTime timestamp) {
        // Hole LintingProject
        Project currLintingProject = projectRepository.findFirstByGitlabProjectId(apiProject.getId());
        if (currLintingProject == null) {
            // Erstelle neues Projekt mit Description und ForkCount
            currLintingProject = new Project(apiProject.getName(), apiProject.getWebUrl(), apiProject.getId(), apiProject.getNamespace().getFullPath(), apiProject.getDescription(), apiProject.getForksCount(), apiProject.getLastActivityAt().toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
            projectRepository.save(currLintingProject);
        } else {
            // Update Description, LastActivity und ForkCount
            currLintingProject.setDescription(apiProject.getDescription());
            currLintingProject.setForkCount(apiProject.getForksCount());
            currLintingProject.setLastCommit(apiProject.getLastActivityAt().toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
        }

        // Erstelle neues Linting Result
        LintingResult lintingResult = new LintingResult(currLintingProject, timestamp);
        lintingResultRepository.save(lintingResult);

        // TODO performance: multithreaded in ThreadPool oder ExecutorService
        runChecks(apiProject, lintingResult);
    }

    private void runChecks(org.gitlab4j.api.models.Project apiProject, LintingResult lintingResult) {
        JsonNode checks = Config.getConfigNode().get("checks");
        for (Iterator<String> it = checks.fieldNames(); it.hasNext(); ) {
            String checkName = it.next();
            JsonNode check = checks.get(checkName);
            // run check
            Check.run(checkName, api, apiProject, checkResultRepository, lintingResult);
        }
    }


}
