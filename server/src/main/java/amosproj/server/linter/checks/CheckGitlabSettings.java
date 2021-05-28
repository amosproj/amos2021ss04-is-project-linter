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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

    //das gewünschte Ergebnis ist false
    public boolean hasSquashedCommitInMergeRequests() {
        var mergeRequestsApi = api.getMergeRequestApi();
        try {
            //hole alle mergeRequests des Projekts
            List<MergeRequest> mergeRequestList = mergeRequestsApi.getMergeRequests(project.getId());
            //checke ob squashing in irgendeinen merge request verwendet wurde
            for (MergeRequest m : mergeRequestList) {
                if (m.getSquash()) {
                    return true;
                }
            }
        } catch (GitLabApiException e) {
            System.out.println("reason: " + e.getReason()); // TODO remove
        }
        return false;
    }

    public boolean hasBadges() {
        try {
            var badgelist = api.getProjectApi().getBadges(project);
            if (!badgelist.isEmpty()) {
                return true;
            }

        } catch (GitLabApiException e) {
            System.out.println(e.getReason());
        }
        return false;
    }

    //checke if squashing im projekt erlaubt ist, dies sollte falsch ergeben
    public boolean hasSquashingEnabled() {
        boolean result = false;
        RepositoryApi repositoryApi = api.getRepositoryApi();
        MergeRequestApi mergeRequestApi = api.getMergeRequestApi();
        Branch demoBranch = null;
        MergeRequest demoMergeRequest = null;
        try {
            //erstelle demo branch und merge request mit squashing erlaubt
            demoBranch = repositoryApi.createBranch(project, "demo", project.getDefaultBranch());
            demoMergeRequest = mergeRequestApi.createMergeRequest(project, demoBranch.getName(), project.getDefaultBranch(), "demoTitle", "demoDescription", null, null, null, null, null, true);
            //squashing ist erlaubt
            result = demoMergeRequest.getSquash();
        } catch (GitLabApiException e) {
            //ein fehler ist passiert oder squashing ist nicht erlaubt
            System.out.println(e.getReason());
        } finally {
            try {
                //lösche die demos falls sie erzeugt werden konnten
                if (demoBranch != null) {
                    repositoryApi.deleteBranch(project, demoBranch.getName());
                }
                if (demoMergeRequest != null) {
                    mergeRequestApi.deleteMergeRequest(project, demoMergeRequest.getIid());
                }
            } catch (GitLabApiException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
