package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import amosproj.server.data.CheckResult;
import amosproj.server.data.CheckSeverity;
import amosproj.server.data.LintingResult;
import amosproj.server.data.Project;
import com.fasterxml.jackson.databind.JsonNode;

import java.lang.reflect.InvocationTargetException;

public class Check {

    protected org.gitlab4j.api.GitLabApi api;
    protected JsonNode config;
    private LintingResult lintingResult;

    // test implementations
    private CheckGitlabFiles filesCheck;
    private CheckGitlabRoles rolesCheck;
    private CheckGitlabSettings settingsCheck;

    public Check(GitLab api) {
        this.api = api;
    }


    /**
     * implementation to run a test via reflection
     *
     * @param node node of the checks.json
     * @return the check result
     */
    public CheckResult runTest(String testName, JsonNode node, Object... args) {
        boolean enabled = node.get("enabled").booleanValue();

        if (!enabled) return null;

        java.lang.reflect.Method method = null;
        boolean checkResult = false;
        try {
            method = getClass().getMethod(testName);
            checkResult = (boolean) method.invoke(this, args);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            return null;
        }
        // return check result
        JsonNode severity = node.get("severity");
        if (severity != null) {
            return new CheckResult(lintingResult, testName, checkResult, CheckSeverity.valueOf(severity.textValue()));
        } else {
            return new CheckResult(lintingResult, testName, checkResult, CheckSeverity.NOT_SPECIFIED);
        }
    }


}