package amosproj.server.linter;

import amosproj.server.GitLab;
import amosproj.server.data.*;
import amosproj.server.linter.checks.CheckGitlabFiles;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class Linter {

    private final GitLab api;

    // autowired
    private final LintingResultRepository lintingResultRepository;
    private final CheckResultRepository checkResultRepository;
    private final ProjectRepository projectRepository;
    // end autowired

    public Linter(GitLab api, LintingResultRepository lintingResultRepository, CheckResultRepository checkResultRepository, ProjectRepository projectRepository) {
        this.api = api;
        this.lintingResultRepository = lintingResultRepository;
        this.checkResultRepository = checkResultRepository;
        this.projectRepository = projectRepository;


        // read configuration file.
        File file = null;
        try {
            file = new ClassPathResource("checks.json").getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
        JsonNode node = null;
        try {
            node = objectMapper.readTree(file);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void runLint(String repoUrl) throws GitLabApiException {
        String path = repoUrl.replace(api.getGitlabHost() + "/", "");

        org.gitlab4j.api.models.Project proj = api.getApi().getProjectApi().getProject(path);
        assert proj != null;
        checkEverything(proj);
    }

    private void checkEverything(org.gitlab4j.api.models.Project apiProject) {
        // Hole LintingProject
        Project currLintingProject = projectRepository.findByGitlabProjectId(apiProject.getId());
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
        CheckResult checkResult = new CheckResult(res, "CheckReadmeExistence", filesChecker.fileExists("readme.md"));
        checkResultRepository.save(checkResult);
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
