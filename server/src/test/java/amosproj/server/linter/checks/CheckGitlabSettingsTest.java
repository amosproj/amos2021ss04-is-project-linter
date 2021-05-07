package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import amosproj.server.data.LintingResult;
import amosproj.server.data.Project;
import amosproj.server.data.ProjectRepository;
import amosproj.server.linter.Linter;
import org.gitlab4j.api.GitLabApiException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


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

    private void preparePositive() throws GitLabApiException {
        prepareSettingsCheck("https://gitlab.cs.fau.de/uv59uxut/linter_positive");
    }

    private void prepareNegative() throws GitLabApiException {
        prepareSettingsCheck("https://gitlab.cs.fau.de/uv59uxut/linter_negative");
    }

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

    // Projekte sollen NICHT das feature request access verwenden
    @Test
    void test_hasRequestAccessEnabled_positive() throws GitLabApiException {
        preparePositive();
        assertFalse(checkGitlabSettings.hasRequestAccessEnabled());
    }

    @Test
    void test_hasRequestAccessEnabled_negative() throws GitLabApiException {
        prepareNegative();
        assertTrue(checkGitlabSettings.hasRequestAccessEnabled());
    }

    // Projekte sollen merge requests erlauben
    @Test
    void test_getMergeRequestsEnabled_positive() throws GitLabApiException {
        preparePositive();
        assertTrue(checkGitlabSettings.hasMergeRequestEnabled());
    }

    @Test
    void test_getMergeRequestsEnabled_negative() throws GitLabApiException {
        prepareNegative();
        assertFalse(checkGitlabSettings.hasMergeRequestEnabled());
    }

    // Projekte sollen issues erlauben
    @Test
    void test_getIssuesEnabled_positive() throws GitLabApiException {
        preparePositive();
        assertTrue(checkGitlabSettings.hasIssuesEnabled());
    }

    @Test
    void test_getIssuesEnabled_negative() throws GitLabApiException {
        prepareNegative();
        assertFalse(checkGitlabSettings.hasIssuesEnabled());
    }

    // Projekte sind public
    @Test
    void test_isPublic_positive() throws GitLabApiException {
        preparePositive();
        assertTrue(checkGitlabSettings.isPublic());
    }

    @Test
    void test_isPublic_negative() throws GitLabApiException {
        prepareNegative();
        assertFalse(checkGitlabSettings.isPublic());
    }

    // Projekte sollen forks erlauben
    @Test
    void test_hasForkingEnabled_positive() throws GitLabApiException {
        preparePositive();
        assertTrue(checkGitlabSettings.hasForkingEnabled());
    }

    @Test
    void test_hasForkingEnabled_negative() throws GitLabApiException {
        prepareNegative();
        assertFalse(checkGitlabSettings.hasForkingEnabled());
    }

}
