package amosproj.server.linter.checks;

import amosproj.server.data.CheckResult;
import amosproj.server.data.CheckResultRepository;
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

    /**
     * Führt einen Test, basierend auf seiner JSON config und seinem TestNamen durch.
     * Diese Methode arbeitet mit Java Reflection um dies passende Methode zu dem (einzigartigem) TestNamen zu finden.
     *
     * @param testName einzigartiger Name des Tests
     * @param node     Eintrag aus der config JSON-Datei
     * @param args     parameter die dem Check übergeben werden sollen (beliebige Anzahl / beliebiger Datentyp)
     * @return Ergebnis, welches automatisch in der Datenbank gespeichert wird.
     */
    public CheckResult runTest(String testName, JsonNode node, Object... args) {
        // nur aktivierte tests sollen ausgeführt werden
        if (!node.get("enabled").booleanValue()) return null;

        // Start Check per reflection
        java.lang.reflect.Method method;
        boolean checkResult;
        try {
            method = getClass().getMethod(testName);
            checkResult = (boolean) method.invoke(this, args);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            return null;
        }
        // return check result
        CheckResult cr = new CheckResult(lintingResult, testName, checkResult);
        checkResultRepository.save(cr);
        return cr;
    }

}
