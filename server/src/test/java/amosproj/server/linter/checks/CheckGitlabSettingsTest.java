package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import amosproj.server.data.LintingResult;
import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import amosproj.server.linter.Linter;
import org.gitlab4j.api.GitLabApiException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class CheckGitlabSettingsTest {

    @Autowired
    private Linter linter;

    @Autowired
    private GitLab api;

    @Autowired
    private ProjectRepository projectRepository;

    private CheckGitlabSettings checkGitlabSettings;

    private void prepareSettingsCheck(String repoUrl) throws GitLabApiException {
        repoUrl = repoUrl.replace("\r", "");
        String path = repoUrl.replace(api.getGitlabHost() + "/", "");

        org.gitlab4j.api.models.Project proj = api.getApi().getProjectApi().getProject(path);
        assert proj != null;

        Project currLintingProject = projectRepository.findByGitlabProjectId(proj.getId());
        if (currLintingProject == null) {
            currLintingProject = new Project(proj.getName(), proj.getWebUrl(), proj.getId(), api.getGitlabHost());
            projectRepository.save(currLintingProject);
        }

        var lintingResult = new LintingResult(currLintingProject, LocalDateTime.now());
        this.checkGitlabSettings = new CheckGitlabSettings(api.getApi(), lintingResult, proj, linter.getConfig());
    }

    @Test
    private void test_isPublic_positive() throws GitLabApiException {
        prepareSettingsCheck("https://gitlab.cs.fau.de/it62ajow/chiefexam");
        assertTrue(checkGitlabSettings.isPublic());
    }

    @Test
    private void test_isPublic_negative() throws GitLabApiException {
        prepareSettingsCheck("https://gitlab.cs.fau.de/it62ajow/chiefexam");
        assertFalse(checkGitlabSettings.isPublic());
    }

}
