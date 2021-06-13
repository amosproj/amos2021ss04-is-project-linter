package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import amosproj.server.data.CheckResult;
import amosproj.server.data.CheckResultRepository;
import amosproj.server.data.LintingResult;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.RepositoryFile;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Check ist die Abstrakte Klasse, die das Ausführen der einzelnen Checks vornimmt.
 */
public abstract class Check {

    protected abstract boolean evaluate(GitLab gitLab, Project project);

    public static void run(String checkName, GitLab gitLab, Project project, CheckResultRepository checkResultRepository, LintingResult lintingResult) {
        boolean result = false;
        try {
            Class<? extends Check> obj = (Class<? extends Check>) Class.forName("amosproj.server.linter.checks." + checkName);
            Method method = obj.getDeclaredMethod("evaluate", GitLab.class, Project.class);
            result = (boolean) method.invoke(gitLab, project);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            // TODO handle some of the exceptions differently (e.g. return false upon ClassNotFound Exception.
            e.printStackTrace();
        }
        CheckResult cr = new CheckResult(lintingResult, checkName, result);
        checkResultRepository.save(cr);
    }

    protected boolean fileExists(GitLab gitLab, Project project, String filepath) {
        try {
            RepositoryFile file = gitLab.getApi().getRepositoryFileApi().getFileInfo(project.getId(), filepath, project.getDefaultBranch());
            if (file == null) return false;
        } catch (GitLabApiException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    protected File getRawFile(GitLab gitLab, Project project, String filepath) {
        if (fileExists(gitLab, project, filepath)) {
            try {
                //lade die Datei nach java.io.tmp
                return gitLab.getApi().getRepositoryFileApi().getRawFile(project.getId(), project.getDefaultBranch(), filepath, null);
            } catch (GitLabApiException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    protected URI getRawReadme(Project project) {
        var readme = project.getReadmeUrl();
        if (readme != null) {
            var raw_readme = readme.replace("blob", "raw");
            try {
                return new URI(raw_readme);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
