package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.models.Project;

public class HasServiceDeskDisabled extends Check {

    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        try {
            JsonNode node = gitLab.makeApiRequest("/projects/" + project.getId());
            return !node.get("service_desk_enabled").asBoolean();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
