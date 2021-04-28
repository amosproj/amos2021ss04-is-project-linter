package amosproj.server.linter.checks;

import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class CheckGitlabSettings {

    private org.gitlab4j.api.models.Project project;
    private JsonNode config;

    public CheckGitlabSettings(org.gitlab4j.api.models.Project project, JsonNode config) {
        this.project = project;
        this.config = config;
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
        return false; // muss noch implementiert werden
    }

    public boolean usesDeveloperRole() {
        return false; // muss noch implementiert werden
    }

    public boolean usesGitLabPages() {
        return false; // muss noch implementiert werden
    }


}
