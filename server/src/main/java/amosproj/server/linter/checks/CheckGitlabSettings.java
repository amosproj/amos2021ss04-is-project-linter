package amosproj.server.linter.checks;

import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.WikisApi;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.ProjectAccess;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class CheckGitlabSettings {

    private org.gitlab4j.api.models.Project project;
    private JsonNode config;
    private GitLabApi api;

    public CheckGitlabSettings(GitLabApi api, org.gitlab4j.api.models.Project project, JsonNode config) {
        this.project = project;
        this.config = config;
        this.api = api;
    }

    public ArrayList<CheckResult> checkAll(LintingResult lintingResult) {
        // TODO run tests according to config TODO
        ArrayList<CheckResult> results = new ArrayList<>();
        for (JsonNode test: config) {
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

    public boolean isPublic() {
        return project.getPublic();
    }

    public boolean hasMergeRequestEnabled() {
        return project.getMergeRequestsEnabled();
    }

    public boolean hasRequestAccess() {
        return project.getRequestAccessEnabled();
    }

    public boolean usesGuestRole() {
        return project.getPermissions().getProjectAccess().getAccessLevel() == AccessLevel.GUEST; // TODO
    }

    public boolean usesDeveloperRole() {
        return project.getPermissions().getProjectAccess().getAccessLevel() == AccessLevel.DEVELOPER; //TODO
    }

    public boolean usesGitLabPages() {
        try {
            return !new WikisApi(api).getPages(project).isEmpty();
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }
        return false;
    }


}
