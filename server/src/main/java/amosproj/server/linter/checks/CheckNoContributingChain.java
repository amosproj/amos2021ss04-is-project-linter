package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.models.Project;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckNoContributingChain extends Check {

    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        // generiere regex
        final var regex = "(?i)(?>https?://)?(?>www.)?(?>[a-zA-Z0-9]+)\\.[a-zA-Z0-9]*\\.[a-z]{3}.*/contributing.md";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        boolean found = false;
        //lade Datei in java.io.tmp
        File file = getRawFile(gitLab, project, "CONTRIBUTING.md");
        URL url = null;
        try {
            if (file != null) url = file.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) {
            try {
                Scanner scanner = new Scanner(url.openStream());
                //lese Zeile der Datei bis Ende
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    // führe regex auf zeile aus
                    final Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        found = true;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // ergebnis umdrehen weil wir auf NO CHAIN prüfen
        return !found;
    }
}
