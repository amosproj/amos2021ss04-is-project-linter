package amosproj.server.linter.checks;

import amosproj.server.data.CheckResultRepository;
import amosproj.server.data.LintingResult;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Project;

/**
 * CheckGitlabRoles implementiert die Checks für Rollen in dem Repository.
 */
public class CheckGitlabRoles extends Check {

    public CheckGitlabRoles(GitLabApi api, Project project, LintingResult lintingResult, CheckResultRepository checkResultRepository) {
        super(api, project, lintingResult, checkResultRepository);
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
