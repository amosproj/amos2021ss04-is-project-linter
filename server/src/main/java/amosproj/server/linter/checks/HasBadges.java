package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;

public class HasBadges extends Check {

    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        try {
            var badgelist = gitLab.getApi().getProjectApi().getBadges(project);
            if (!badgelist.isEmpty()) {
                return true;
            }
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
        return false;
    }
}
