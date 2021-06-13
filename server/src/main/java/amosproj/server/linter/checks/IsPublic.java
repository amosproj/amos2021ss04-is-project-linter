package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.Visibility;

public class IsPublic extends Check {


    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        return project.getVisibility() == Visibility.PUBLIC;
    }
}
