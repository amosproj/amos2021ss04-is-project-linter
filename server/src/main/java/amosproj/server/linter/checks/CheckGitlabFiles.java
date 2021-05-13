package amosproj.server.linter.checks;

import amosproj.server.data.LintingResult;
import com.fasterxml.jackson.databind.JsonNode;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class CheckGitlabFiles {

    private org.gitlab4j.api.models.Project proj;
    private GitLabApi api;

    /////////////////
    ///// TESTS /////
    /////////////////

    public boolean checkReadmeExistence() {
        return proj.getReadmeUrl() != null;
    }

    public boolean checkContributingExistence() {
        return fileExists("CONTRIBUTING.md");
    }

    public boolean checkMaintainersExistence() {
        return fileExists("MAINTAINERS.md");
    }

    public boolean fileExists(String filepath) {
        try {
            RepositoryFile file = api.getRepositoryFileApi().getFileInfo(proj.getId(), filepath, proj.getDefaultBranch());
            if (file == null) return false;
        } catch (GitLabApiException | IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    public boolean checkContributingHasLinks(){
        //lade Datei in java.io.tmp
        File file = getRawFile("CONTRIBUTING.md");
        if(file != null) {
            try{
                Scanner scanner = new Scanner(file);
                //lese Zeile der Datei bis Ende
                while (scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    //suche in Zeile nach gewünschten Inhalt
                    //this needs to be adjusted
                    if(line.matches(".*CONTRIBUTING.md.*")){
                        return true;
                    }
                }
            } catch (FileNotFoundException e) {

            }
        }
        return false;
    }

    public boolean checkReadmeHasLinks(){
        //lade Datei in java.io.tmp
        //adjust this to work with proj.getReadmeUrl() instead of README.md
        File file = getRawFile("README.md");
        if(file != null) {
            try{
                Scanner scanner = new Scanner(file);
                //lese Zeile der Datei bis Ende
                while (scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    //suche in Zeile nach gewünschten Inhalt
                    //this needs to be adjusted
                    if(line.matches(".*http://.*\\..*")){
                        return true;
                    }
                }
            } catch (FileNotFoundException e) {

            }
        }
        return false;
    }

    public File getRawFile(String filepath) {
        if(fileExists(filepath)) {
            try{
                //lade die Datei nach java.io.tmp
                File file = api.getRepositoryFileApi().getRawFile(proj.getId(), proj.getDefaultBranch(), filepath, null);
                return file;
            } catch (GitLabApiException e) {
                System.out.println("reason: " + e.getReason());
            }
        }
        return null;
    }

}
