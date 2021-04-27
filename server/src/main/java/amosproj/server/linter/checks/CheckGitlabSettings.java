package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.GitLabApiException;
//import org.gitlab4j.api.models.Visibility;
import org.springframework.beans.factory.annotation.Autowired;

import java.beans.Visibility;

public class CheckGitlabSettings{

    @Autowired
    private GitLab api;
    org.gitlab4j.api.models.Project project;

    public CheckGitlabSettings(org.gitlab4j.api.models.Project project){
        this.project = project;
    }
    /*public boolean isPublic(String apiUrl) {
        // call api
        org.gitlab4j.api.models.Project project = null;
        try {
            project = api.getApi().getProjectApi().getProject(apiUrl);
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }

        assert project != null;
        return project.getVisibility() == Visibility.PUBLIC;
    }*/


    public boolean checkValue(org.gitlab4j.api.models.Project project, String field, String expected) {
        return false;
    }

    public boolean isPublic(){
        return project.getPublic();
    }

    public boolean hasRequestAccess() {
        return project.getRequestAccessEnabled();
    }

    public boolean usesGuestRole() {
        return false; // muss noch implementiert werden
    }

    public boolean usesDeveloperRole() {
        return false; // muss noch implementiert werden
    }

    public boolean usesGitLabPages() {
        return false; // muss noch implementiert werden
    }
}
