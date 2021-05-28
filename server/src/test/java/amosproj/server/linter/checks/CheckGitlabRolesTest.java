package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import amosproj.server.data.CheckResultRepository;
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

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
public class CheckGitlabRolesTest {
    @Autowired
    private GitLab api;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CheckResultRepository checkResultRepository;

    private CheckGitlabRoles checkGitlabRoles;

    private void prepareSettingsCheck(String repoUrl) throws GitLabApiException {
        repoUrl = repoUrl.replace("\r", "");
        String path = repoUrl.replace(api.getGitlabHost() + "/", "");

        org.gitlab4j.api.models.Project proj = api.getApi().getProjectApi().getProject(path);
        assert proj != null;

        Project currLintingProject = projectRepository.findFirstByGitlabProjectId(proj.getId());
        if (currLintingProject == null) {
            currLintingProject = new Project(proj.getName(), proj.getWebUrl(), proj.getId(), api.getGitlabHost());
            projectRepository.save(currLintingProject);
        }

        var lintingResult = new LintingResult(currLintingProject, LocalDateTime.now());
        this.checkGitlabRoles = new CheckGitlabRoles(api, proj, lintingResult, checkResultRepository);
    }

    @Test
    public void test_developerRoleDisabled_positive() throws GitLabApiException {
        prepareSettingsCheck("https://gitlab.cs.fau.de/ib49uquh/amos-testz");
        assertTrue(checkGitlabRoles.developerRoleDisabled());
    }

    @Test
    public void test_developerRoleDisabled_negative() throws GitLabApiException {
        prepareSettingsCheck("https://gitlab.cs.fau.de/bo63gazu/amos-test-project");
        assertFalse(checkGitlabRoles.developerRoleDisabled());
    }

    @Test
    public void test_guestRoleDisabled_positive() throws GitLabApiException {
        prepareSettingsCheck("https://gitlab.cs.fau.de/ib49uquh/amos-testz");
        assertTrue(checkGitlabRoles.guestRoleDisabled());
    }

    @Test
    public void test_guestRoleDisabled_negative() throws GitLabApiException {
        prepareSettingsCheck("https://gitlab.cs.fau.de/bo63gazu/amos-test-project");
        assertFalse(checkGitlabRoles.guestRoleDisabled());
    }
}
