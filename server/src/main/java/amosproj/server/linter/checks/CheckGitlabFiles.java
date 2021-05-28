package amosproj.server.linter.checks;

import amosproj.server.GitLab;
import amosproj.server.data.CheckResultRepository;
import amosproj.server.data.LintingResult;
import amosproj.server.linter.Linter;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.RepositoryFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CheckGitlabFiles implementiert die Checks für Dateien in dem Repository.
 */
public class CheckGitlabFiles extends Check {

    private GitLabApi api;
    public CheckGitlabFiles(GitLab gitLab, Project project, LintingResult lintingResult, CheckResultRepository checkResultRepository) {
        super(gitLab, project, lintingResult, checkResultRepository);
        this.api = gitLab.getApi();
    }

    /////////////////
    ///// TESTS /////
    /////////////////

    public boolean checkReadmeExistence() {
        return project.getReadmeUrl() != null;
    }

    public boolean checkContributingExistence() {
        return fileExists("CONTRIBUTING.md");
    }

    public boolean checkMaintainersExistence() {
        return fileExists("MAINTAINERS.md");
    }

    public boolean fileExists(String filepath) {
        try {
            RepositoryFile file = api.getRepositoryFileApi().getFileInfo(project.getId(), filepath, project.getDefaultBranch());
            if (file == null) return false;
        } catch (GitLabApiException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * Überprüft ob in der CONTRIBUTING.MD keine verlinkung auf eine andere CONTRIBUTING.MD ist.
     * <p>
     * Lädt sich die Datei aus dem Repository herunter,
     * überprüft zeile für zeile ob der regex darauf anspringt
     *
     * @return True || False
     */
    public boolean checkNoContributingChain() {
        // generiere regex
        final var regex = "(?i)(?>https?://)?(?>www.)?(?>[a-zA-Z0-9]+)\\.[a-zA-Z0-9]*\\.[a-z]{3}.*/contributing.md";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        boolean found = false;
        //lade Datei in java.io.tmp
        File file = getRawFile("CONTRIBUTING.md");
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

    /**
     * Überprüft ob sich in der README.MD ein link zu entweder confluence oder datev documentation befindet
     * <p>
     * Lädt die Datein herunter, überprüft zeile für zeile ob der regex anspringt, returned ergebniss
     *
     * @return TRUE || FALSE
     */
    public boolean checkReadmeHasLinks() {
        // generiere regex
        JsonNode links = Linter.getConfigNode().get("settings").get("readMeLinks");
        for (JsonNode it : links) {
            String link = it.asText();
            final Pattern pattern = Pattern.compile(link, Pattern.MULTILINE);

            //lade Datei in java.io.tmp
            URI uri = getRawReadme();
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

    public URI getRawReadme() {
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

    public File getRawFile(String filepath) {
        if (fileExists(filepath)) {
            try {
                //lade die Datei nach java.io.tmp
                return api.getRepositoryFileApi().getRawFile(project.getId(), project.getDefaultBranch(), filepath, null);
            } catch (GitLabApiException e) {
                System.out.println("reason: " + e.getReason()); // TODO remove
            }
        }
        return null;
    }

}
