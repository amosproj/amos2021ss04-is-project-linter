package amosproj.server.linter.checks;

import amosproj.server.data.CheckResult;
import amosproj.server.data.CheckResultRepository;
import amosproj.server.data.CheckSeverity;
import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Project;

import java.lang.reflect.InvocationTargetException;

/**
 * Check ist die Abstrakte Klasse, die das Ausführen der einzelnen Checks vornimmt.
 */
public abstract class Check {

    protected org.gitlab4j.api.GitLabApi api;
    protected org.gitlab4j.api.models.Project project;
    private final LintingResult lintingResult;
    private final CheckResultRepository checkResultRepository;

    protected Check(GitLabApi api, Project project, LintingResult lintingResult, CheckResultRepository checkResultRepository) {
        this.api = api;
        this.project = project;
        this.lintingResult = lintingResult;
        this.checkResultRepository = checkResultRepository;
    }

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
            CheckResult cr = new CheckResult(lintingResult, testName, checkResult, CheckSeverity.valueOf(severity.textValue()));
            checkResultRepository.save(cr);
            return cr;
        } else {
            CheckResult cr = new CheckResult(lintingResult, testName, checkResult, CheckSeverity.NOT_SPECIFIED);
            checkResultRepository.save(cr);
            return cr;
        }
    }

}