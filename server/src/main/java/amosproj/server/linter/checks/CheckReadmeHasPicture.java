package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import org.gitlab4j.api.models.Project;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckReadmeHasPicture extends Check {

    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        // regex expressions
        final Pattern pattern_md = Pattern.compile("\\!\\[.*\\]\\(.*\\)", Pattern.CASE_INSENSITIVE);
        final Pattern pattern_html = Pattern.compile("<img.*src=\".*\".*>", Pattern.CASE_INSENSITIVE);
        //lade Datei in java.io.tmp
        URI uri = getRawReadme(project);
        if (uri != null) {
            try {
                Scanner scanner = new Scanner(uri.toURL().openStream());
                //lese Zeile der Datei bis Ende
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    // führe regex auf zeile aus
                    final Matcher matcher_md = pattern_md.matcher(line);
                    final Matcher matcher_html = pattern_html.matcher(line);
                    if (matcher_html.find() || matcher_md.find()) {
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

}
