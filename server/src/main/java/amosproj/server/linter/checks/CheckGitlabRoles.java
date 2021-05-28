package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import amosproj.server.data.CheckResultRepository;
import amosproj.server.data.LintingResult;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;

import java.util.LinkedList;
import java.util.List;

/**
 * CheckGitlabRoles implementiert die Checks für Rollen in dem Repository.
 */
public class CheckGitlabRoles extends Check {

    private List<Member> list;

    public CheckGitlabRoles(GitLab gitLab, Project project, LintingResult lintingResult, CheckResultRepository checkResultRepository) {
        super(gitLab, project, lintingResult, checkResultRepository);
        try {
            this.list = gitLab.getApi().getProjectApi().getMembers(project.getId());
        } catch (GitLabApiException e) {
            e.printStackTrace();
            this.list = null;
        }
    }


    /////////////////
    ///// TESTS /////
    /////////////////

    public boolean guestRoleDisabled() {
        for (Member member : list) {
            if (member.getAccessLevel() == AccessLevel.GUEST)
                return false;
        }
        return true;
    }

    public boolean developerRoleDisabled() {
        for (Member member : list) {
            if (member.getAccessLevel() == AccessLevel.DEVELOPER)
                return false;
        }
        return true;
    }

}
