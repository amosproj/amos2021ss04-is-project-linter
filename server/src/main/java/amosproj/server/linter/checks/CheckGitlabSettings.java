package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import amosproj.server.data.CheckResultRepository;
import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.MergeRequestApi;
import org.gitlab4j.api.RepositoryApi;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.Visibility;

import java.util.List;

/**
 * CheckGitlabSettings implementiert die Checks für Einstellungen in dem Repository.
 */
public class CheckGitlabSettings extends Check {

    private GitLabApi api;

    public CheckGitlabSettings(GitLab gitLab, Project project, LintingResult lintingResult, CheckResultRepository checkResultRepository) {
        super(gitLab, project, lintingResult, checkResultRepository);
        this.api = gitLab.getApi();
    }


    /////////////////
    ///// TESTS /////
    /////////////////

    public boolean isPublic() {
        return project.getVisibility() == Visibility.PUBLIC;
    }

    /**
     * Liest forking_access_level aus und returned, ob es auf disabled gesetzt ist oder nicht.
     * Dabei spielt es keine Rolle, ob es enabled oder private ist.
     *
     * @return Ob forking_access_level auf disabled gesetzt ist.
     */
    public boolean hasForkingEnabled() {
        try {
            JsonNode node = gitLab.makeApiRequest("/projects/" + project.getId());
            String forkingAccessLevel = node.get("forking_access_level").asText();
            if (forkingAccessLevel.equals("disabled"))
                return false;
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasMergeRequestEnabled() {
        return project.getMergeRequestsEnabled();
    }

    public boolean hasRequestAccessEnabled() {
        return project.getRequestAccessEnabled();
    }

    public boolean hasIssuesEnabled() {
        return project.getIssuesEnabled();
    }

    public boolean gitlabWikiDisabled() {
        return !project.getWikiEnabled();
    }

    public boolean hasAvatar() {
        return project.getAvatarUrl() != null;
    }

    public boolean hasDescription() {
        var description = project.getDescription();
        return (description != null && !description.equals(""));
    }

    public boolean hasBadges() {
        try {
            var badgelist = api.getProjectApi().getBadges(project);
            if (!badgelist.isEmpty()) {
                return true;
            }
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasSquashingDisabled() {
        try {
            JsonNode node = gitLab.makeApiRequest("/projects/" + project.getId());
            String forkingAccessLevel = node.get("squash_option").asText();
            if (forkingAccessLevel.equals("default_off"))
                return true;
            return false;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasServiceDeskDisabled() {
        try {
            JsonNode node = gitLab.makeApiRequest("/projects/" + project.getId());
            return !node.get("service_desk_enabled").asBoolean();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

}
