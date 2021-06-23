package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.models.Project;

public class HasSquashingDisabled extends Check {

    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        try {
            JsonNode node = gitLab.makeApiRequest("/projects/" + project.getId());
            JsonNode squashNode = node.get("squash_option");
            if (squashNode == null) {
                logger.warn("Die GitLab Instanz läuft nicht auf der neuesten version. Squashing ist nicht direkt auslesbar.");
                return false;
            }
            String forkingAccessLevel = squashNode.asText();
            return forkingAccessLevel.equals("default_off");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
