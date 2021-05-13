package amosproj.server.linter.checks;

import org.gitlab4j.api.models.AccessLevel;

public class CheckGitlabRoles {

    private org.gitlab4j.api.models.Project project;


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
