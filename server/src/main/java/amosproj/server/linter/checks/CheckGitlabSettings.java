package amosproj.server.linter.checks;

import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.Visibility;

import java.util.List;

public class CheckGitlabSettings extends Check {

    private org.gitlab4j.api.models.Project project;

    public CheckGitlabSettings(GitLabApi api, LintingResult lintingResult, org.gitlab4j.api.models.Project project, JsonNode config) {
        super(api, lintingResult, config);
        this.project = project;
    }

    /////////////////
    ///// TESTS /////
    /////////////////

    public boolean isPublic() {
        return project.getVisibility() == Visibility.PUBLIC;
    }

    public boolean hasForkingEnabled() {
        // Initialisiere Objekte für namespace
        var projectApi = api.getProjectApi();
        Project forkproj = new Project();
        boolean hasForksEnabled = false;

        // versuche Projekt zu forken um zu überpürfen ob forks erlaubt sind
        try {
            // hole namespace
            var namespace = api.getNamespaceApi().getNamespaces().get(0).getFullPath();
            // versuche projekt zu forken
            forkproj = projectApi.forkProject(project, namespace, "forktest", "forktests");
            // wenn hier kein fehler kam, is forking erlaubt
            hasForksEnabled = true;
        } catch (GitLabApiException e) {
            System.out.println("reason: " + e.getReason());
        } finally {
            try {
                // lösche überreste der versuchs zu forken
                if (hasForksEnabled) projectApi.deleteProject(forkproj.getId());
            } catch (GitLabApiException e) {
                e.printStackTrace();
            }
        }
        return hasForksEnabled;
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

    public boolean gitlabWikiEnabled() {
        try {
            return !(api.getWikisApi().getPages(project).isEmpty());
        } catch (GitLabApiException e) {
            return false;
        }
    }

    public boolean hasAvatar(){
        return project.getAvatarUrl() != null;
    }

    public boolean hasDescription() {
        return (project.getDescription() != null && project.getDescription() != "");
    }

    public boolean hasSquashingEnabled() {
        var mergeRequestsApi = api.getMergeRequestApi();
        boolean hasSquashingEnabled = false;

        try{
            //hole alle mergeRequests des Projekts
            List<MergeRequest> mergeRequestList = mergeRequestsApi.getMergeRequests(project.getId());
            for (MergeRequest m : mergeRequestList){
                //wenn in squashing in irgendeinen merge request verwendet wurde ist squashing erlaubt
                if(m.getSquash()){
                    return true;
                }
            }
        } catch (GitLabApiException e){
            System.out.println("reason: " + e.getReason());
        }
        return hasSquashingEnabled;
    }

}
