package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.models.Project;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

public class NotDefaultReadme extends Check {


    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        String defaultReadme = "# " + project.getName(); // Sollte sich die Default Readme ändern, diese Zeile
        // anpassen. Dabei "\n" und "\r" ignorieren.
        if (project.getReadmeUrl() == null)
            return false;
        URI uri = getRawReadme(project);
        if (uri != null) {
            try {
                Scanner scanner = new Scanner(uri.toURL().openStream());
                String line = "";
                while (scanner.hasNextLine()) {
                    line += scanner.nextLine();
                }

                return !line.equals(defaultReadme);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
