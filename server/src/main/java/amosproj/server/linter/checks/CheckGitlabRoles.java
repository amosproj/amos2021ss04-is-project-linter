package amosproj.server.linter.checks;

import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.AccessLevel;

import java.util.LinkedList;
import java.util.List;

public class CheckGitlabRoles extends Check {

    private org.gitlab4j.api.models.Project project;
    private JsonNode config;


    public CheckGitlabRoles(GitLabApi api, LintingResult lintingResult, org.gitlab4j.api.models.Project project, JsonNode config) {
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
                CheckResult ch = runTest(testName);
                if (ch != null) res.add(ch);
            }
        }
        return res;
    }


    /////////////////
    ///// TESTS /////
    /////////////////

    public boolean guestRoleEnabled() {
        return project.getPermissions().getProjectAccess().getAccessLevel() == AccessLevel.GUEST;
    }

    public boolean developerRoleEnabled() {
        return project.getPermissions().getProjectAccess().getAccessLevel() == AccessLevel.DEVELOPER;
    }

}
