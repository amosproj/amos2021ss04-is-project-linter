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
public class CheckGitlabFilesTest {

    @Autowired
    private Linter linter;

    @Autowired
    private GitLab api;

    @Autowired
    private ProjectRepository projectRepository;

    private CheckGitlabFiles checkGitlabFiles;

    private void preparePositive() throws GitLabApiException {
        prepareSettingsCheck("https://gitlab.cs.fau.de/or16iqyd/hasReadme");
    }

    private void prepareNegative() throws GitLabApiException {
        prepareSettingsCheck("https://gitlab.cs.fau.de/or16iqyd/noReadme");
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
        this.checkGitlabFiles = new CheckGitlabFiles(api.getApi(), lintingResult, proj, linter.getConfig());
    }

    @Test
    void test_hasReadme_positive() throws GitLabApiException {
        preparePositive();
        assertTrue(checkGitlabFiles.checkReadmeExistence());
    }

    @Test
    void test_hasReadme_negative() throws GitLabApiException {
        prepareNegative();
        assertFalse(checkGitlabFiles.checkReadmeExistence());
    }

    @Test
    void test_hasContributing_positive() throws GitLabApiException {
        preparePositive();
        assertTrue(checkGitlabFiles.checkContributingExistence());
    }

    @Test
    void test_hasContributing_negative() throws GitLabApiException {
        prepareNegative();
        assertFalse(checkGitlabFiles.checkContributingExistence());
    }

    @Test
    void test_hasMaintainers_positive() throws GitLabApiException {
        preparePositive();
        assertTrue(checkGitlabFiles.checkMaintainersExistence());
    }

    @Test
    void test_hasMaintainers_negative() throws GitLabApiException {
        prepareNegative();
        assertFalse(checkGitlabFiles.checkMaintainersExistence());
    }

    @Test
    void test_hasLinksInContributing_positive() throws GitLabApiException {
        preparePositive();
        assertTrue(checkGitlabFiles.checkContributingHasLinks());
    }

    @Test
    void test_hasLinksInContributing_negative() throws GitLabApiException {
        prepareNegative();
        assertFalse(checkGitlabFiles.checkContributingHasLinks());
    }

    @Test
    void test_hasLinksInReadme_positive() throws GitLabApiException {
        preparePositive();
        assertTrue(checkGitlabFiles.checkReadmeHasLinks());
    }

    @Test
    void test_hasLinksInReadme_negative() throws GitLabApiException {
        prepareNegative();
        assertFalse(checkGitlabFiles.checkReadmeHasLinks());
    }

}
