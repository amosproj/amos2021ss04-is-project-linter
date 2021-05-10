package amosproj.server.linter.checks;

import amosproj.server.data.CheckResult;
import amosproj.server.data.CheckSeverity;
import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class provides a simplified interface to a Check
 */
public abstract class Check {

    protected org.gitlab4j.api.GitLabApi api;
    protected JsonNode config;
    private LintingResult lintingResult;

    /**
     * Generic constructor for Check
     *
     * @param api
     * @param lintingResult
     */
    public Check(GitLabApi api, LintingResult lintingResult, JsonNode config) {
        this.api = api;
        this.lintingResult = lintingResult;
        this.config = config;
    }

    /**
     * implementation to run a test via reflection
     *
     * @param node node of the checks.json
     * @return the check result
     */
    protected CheckResult runTest(JsonNode node, Object... args) {
        java.lang.reflect.Method method = null;
        boolean checkResult = false;
        String testName = node.get("name").textValue();
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

    /**
     * Runs through all of the checks
     * @return A LinkedList of CheckResults
     */
    public List<CheckResult> checkAll() {
        LinkedList<CheckResult> res = new LinkedList<>();
        for (JsonNode c : config) {
            boolean enabled = c.get("enabled").booleanValue();
            if (enabled) {
                CheckResult ch = runTest(c);
                if (ch != null) res.add(ch);
            }
        }
        return res;
    }

}
