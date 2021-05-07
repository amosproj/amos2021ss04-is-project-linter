package amosproj.server.linter.checks;

import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.ProjectApi;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.Visibility;

import java.util.LinkedList;
import java.util.List;

public class CheckGitlabSettings extends Check {

    private org.gitlab4j.api.models.Project project;
    private JsonNode config;

    public CheckGitlabSettings(GitLabApi api, LintingResult lintingResult, org.gitlab4j.api.models.Project project, JsonNode config) {
        super(api, lintingResult);
        this.project = project;
        this.config = config;
    }

    public List<CheckResult> checkAll() {
        LinkedList<CheckResult> res = new LinkedList<>();
        for (JsonNode c : config) {
            String testName = c.get("name").textValue();
            boolean enabled = c.get("enabled").booleanValue();
            if (enabled) {
                CheckResult ch = runTest(testName, c);
                if (ch != null) res.add(ch);
            }
        }
        return res;
    }

    /////////////////
    ///// TESTS /////
    /////////////////

    public boolean isPublic() {
        return project.getVisibility() == Visibility.PUBLIC;
    }

    public boolean hasForkingEnabled() {
        // Initialisiere Objekte für namespace
        var projectApi = api.getProjectApi();
        Project createproj = new Project();
        Project forkproj = new Project();
        boolean hasForksEnabled = false;

        // versuche Projekt zu forken um zu überpürfen ob forks erlaubt sind
        try {
            // erstelle projekt um namespace holen zu können
            createproj = projectApi.createProject("test123", "test123");
            // versuche projekt zu forken
            forkproj = projectApi.forkProject(project, createproj.getNamespace().getFullPath(), "forktest", "forktests");
            // wenn hier kein fehler kam, is forking erlaubt
            hasForksEnabled = true;
        } catch (GitLabApiException e) {
            System.out.println(e.getReason());
        } finally {
            try {
                // lösche überreste der versuchs zu forken
                projectApi.deleteProject(createproj.getId());
                if (hasForksEnabled) projectApi.deleteProject(forkproj.getId());
            } catch (GitLabApiException e) {
                e.printStackTrace();
            }
        }
        return hasForksEnabled;
    }

    public boolean hasMergeRequestEnabled() {
        return project.getMergeRequestsEnabled();
    }

    public boolean hasRequestAccessEnabled() {
        return project.getRequestAccessEnabled();
    }

    public boolean hasIssuesEnabled() {
        return project.getIssuesEnabled();
    }

    public boolean gitlabPagesEnabled() {
        try {
            return !(api.getWikisApi().getPages(project).isEmpty());
        } catch (GitLabApiException e) {
            return false;
        }
    }


}
