package amosproj.server.linter.checks;

import amosproj.server.data.CheckResult;
import amosproj.server.data.CheckResultRepository;
import amosproj.server.data.LintingResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Check ist die Abstrakte Klasse, die das Ausführen der einzelnen Checks vornimmt.
 */
public abstract class Check {

    protected abstract boolean evaluate();

    /**
     * runs a Check Based on its name. Uses reflection.
     *
     * @param checkName
     * @param checkResultRepository
     * @param lintingResult
     */
    public static void run(String checkName, CheckResultRepository checkResultRepository, LintingResult lintingResult) {
        boolean result = false;
        try {
            Class<? extends Check> obj = (Class<? extends Check>) Class.forName("amosproj.server.linter.checks." + checkName);
            Method method = obj.getDeclaredMethod("evaluate");
            result = (boolean) method.invoke(obj.getConstructor().newInstance());
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            // TODO handle some of the exceptions differently (e.g. return false upon ClassNotFound Exception.
            e.printStackTrace();
        }
        CheckResult cr = new CheckResult(lintingResult, checkName, result);
        checkResultRepository.save(cr);
    }

}
