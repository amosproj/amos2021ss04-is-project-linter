package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;

import java.util.List;

public class DeveloperRoleDisabled extends Check {

    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        List<Member> list;
        try {
            list = gitLab.getApi().getProjectApi().getMembers(project.getId());
        } catch (GitLabApiException e) {
            e.printStackTrace();
            list = null;
        }

        for (Member member : list) {
            if (member.getAccessLevel() == AccessLevel.DEVELOPER)
                return false;
        }
        return true;
    }
}
