package amosproj.server.linter.checks;

import amosproj.server.data.CheckResult;
import amosproj.server.data.LintingResult;
import org.gitlab4j.api.GitLabApi;

import java.lang.reflect.InvocationTargetException;

/**
 * This class provides a simplified interface to a Check
 */
public abstract class Check {

    protected org.gitlab4j.api.GitLabApi api;
    private LintingResult lintingResult;

    /**
     * Generic constructor for a test, setting up the api connection.
     *
     * @param api Connection GitLab api
     */
    public Check(GitLabApi api, LintingResult lintingResult) {
        this.api = api;
        this.lintingResult = lintingResult;
    }

    /**
     * implementation to run a test via reflection
     *
     * @param testName name of the test to run (see config)
     * @return the check result
     */
    protected CheckResult runTest(String testName, Object... args) {
        java.lang.reflect.Method method = null;
        boolean checkResult = false;
        try {
            method = getClass().getMethod(testName);
            checkResult = (boolean) method.invoke(this, args);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return new CheckResult(lintingResult, testName, checkResult);
    }

}
