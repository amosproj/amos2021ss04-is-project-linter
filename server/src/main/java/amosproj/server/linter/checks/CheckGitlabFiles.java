package amosproj.server.linter.checks;

import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;

import java.util.ArrayList;

public class CheckGitlabFiles extends Check {

    private org.gitlab4j.api.models.Project proj;
    private JsonNode config;

    public CheckGitlabFiles(GitLabApi api, LintingResult lintingResult, org.gitlab4j.api.models.Project project, JsonNode config) {
        super(api, lintingResult);
        this.proj = project;
        this.config = config;
    }

    public ArrayList<CheckResult> checkAll() {
        ArrayList<CheckResult> results = new ArrayList<>();
        for (JsonNode test : config) {
            String testName = test.get("name").textValue();
            boolean enabled = test.get("enabled").booleanValue();
            if (enabled) {
                CheckResult ch = runTest(testName);
                if (ch != null) results.add(ch);
            }
        }
        return results;
    }

    /////////////////
    ///// TESTS /////
    /////////////////

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
