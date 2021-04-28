package amosproj.server.linter.checks;

import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class CheckGitlabFiles {

    private GitLabApi api;
    private org.gitlab4j.api.models.Project proj;
    private JsonNode config;

    public CheckGitlabFiles(GitLabApi api, org.gitlab4j.api.models.Project project, JsonNode config) {
        this.api = api;
        this.proj = project;
        this.config = config;
    }

    public ArrayList<CheckResult> checkAll(LintingResult lintingResult) {
        // TODO run tests according to config TODO
        ArrayList<CheckResult> results = new ArrayList<>();
        for (JsonNode test : config) {
            String testName = test.get("name").textValue();
            boolean enabled = test.get("enabled").booleanValue();


            if (enabled) {
                java.lang.reflect.Method method = null;
                boolean checkResultBoolean = false;
                try {
                    method = getClass().getMethod(testName);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                try {
                    checkResultBoolean = (boolean) method.invoke(this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                CheckResult checkResult = new CheckResult(lintingResult, testName, checkResultBoolean);
                results.add(checkResult);
            }
        }
        return results;
    }

    public boolean checkReadmeExistence() {
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
