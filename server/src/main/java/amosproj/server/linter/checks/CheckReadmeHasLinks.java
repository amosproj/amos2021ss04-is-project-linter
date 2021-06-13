package amosproj.server.linter.checks;

import amosproj.server.Config;
import amosproj.server.GitLab;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.models.Project;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CheckReadmeHasLinks extends Check {

    @Override
    protected boolean evaluate(GitLab gitLab, Project project) {
        // generiere regex
        JsonNode links = Config.getConfigNode().get("settings").get("readMeLinks");
        for (JsonNode it : links) {
            String link = it.asText();
            final Pattern pattern = Pattern.compile(link, Pattern.MULTILINE);

            //lade Datei in java.io.tmp
            URI uri = getRawReadme(project);
            if (uri != null) {
                try {
                    Scanner scanner = new Scanner(uri.toURL().openStream());
                    //lese Zeile der Datei bis Ende
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        // führe regex auf zeile aus
                        final Matcher matcher = pattern.matcher(line);
                        if (matcher.find()) {
                            return true;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
