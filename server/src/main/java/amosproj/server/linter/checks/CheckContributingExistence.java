package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.models.Project;

public class CheckContributingExistence extends Check {

    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        return fileExists(gitLab, project, "CONTRIBUTING.md");
    }
}
