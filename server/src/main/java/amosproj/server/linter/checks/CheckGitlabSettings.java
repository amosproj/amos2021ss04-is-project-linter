package amosproj.server.linter.checks;

import amosproj.server.data.CheckResultRepository;
import amosproj.server.data.LintingResult;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.Visibility;

import java.util.List;

public class CheckGitlabSettings extends Check {

    public CheckGitlabSettings(GitLabApi api, Project project, LintingResult lintingResult, CheckResultRepository checkResultRepository) {
        super(api, project, lintingResult, checkResultRepository);
    }


    /////////////////
    ///// TESTS /////
    /////////////////

    public boolean isPublic() {
        return project.getVisibility() == Visibility.PUBLIC;
    }

    public boolean hasForkingEnabled() throws GitLabApiException {
        // Initialisiere Objekte für namespace
        var projectApi = api.getProjectApi();
        Project forkproj = new Project();
        String namespace = "";
        boolean hasForksEnabled = false;
        boolean forkingConflict = false;

        // versuche Projekt zu forken um zu überpürfen ob forks erlaubt sind
        try {
            // hole namespace
            namespace = api.getNamespaceApi().getNamespaces().get(0).getFullPath();
            // versuche projekt zu forken
            forkproj = projectApi.forkProject(project, namespace, "forktest", "forktest");
            // wenn hier kein fehler kam, is forking erlaubt
            hasForksEnabled = true;
        } catch (GitLabApiException e) {
            System.out.println("reason: " + e.getReason());
            if (e.getReason().equals("Conflict")) forkingConflict = true;
        } finally {
            try {
                // lösche überreste der versuchs zu forken
                if (hasForksEnabled) projectApi.deleteProject(forkproj.getId());
            } catch (GitLabApiException e) {
                e.printStackTrace();
            }
        }
        if (forkingConflict) {
            var conflictProject = projectApi.getProject(namespace, "forktest");
            projectApi.deleteProject(conflictProject);
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
            System.out.println("reason: " + e.getReason());
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

}
