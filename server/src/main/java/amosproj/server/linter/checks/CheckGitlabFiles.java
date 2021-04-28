package amosproj.server.linter.checks;

import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;

import java.lang.reflect.InvocationTargetException;

public class CheckGitlabFiles {

    private GitLabApi api;
    private org.gitlab4j.api.models.Project proj;

    public CheckGitlabFiles(GitLabApi api, org.gitlab4j.api.models.Project project, JsonNode config) {
        this.api = api;
        this.proj = project;
        // TODO run tests according to config TODO
        String testName = config.get(0).get("name").textValue();
        boolean enabled = config.get(0).get("enabled").booleanValue();

        if (enabled) {
            java.lang.reflect.Method method = null;
            try {
                method = getClass().getMethod(testName);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                method.invoke(this);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }

    public boolean checkReadmeExiststence() {
        // TODO make case insensitive.
        return fileExists("readme.me");
    }


    public boolean fileExists(String filepath) {
        try {
            RepositoryFile file = api.getRepositoryFileApi().getFileInfo(proj.getId(), filepath, "master");
        } catch (GitLabApiException e) {
            return false;
        }
        return true;
    }

}
