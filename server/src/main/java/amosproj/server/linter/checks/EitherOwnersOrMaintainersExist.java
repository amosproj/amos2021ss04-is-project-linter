package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.models.Project;

public class EitherOwnersOrMaintainersExist extends Check {

    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        return fileExists(gitLab, project, "OWNERS.md") || fileExists(gitLab, project, "MAINTAINERS.md");
    }
}
