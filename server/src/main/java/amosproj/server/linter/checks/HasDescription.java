package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.models.Project;

public class HasDescription extends Check {

    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        var description = project.getDescription();
        return (description != null && !description.equals(""));
    }
}
