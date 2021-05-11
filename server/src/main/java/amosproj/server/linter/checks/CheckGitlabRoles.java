package amosproj.server.linter.checks;

import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.AccessLevel;

public class CheckGitlabRoles extends Check {

    private org.gitlab4j.api.models.Project project;


    public CheckGitlabRoles(GitLabApi api, LintingResult lintingResult, org.gitlab4j.api.models.Project project, JsonNode config) {
        super(api, lintingResult, config);
        this.project = project;
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
