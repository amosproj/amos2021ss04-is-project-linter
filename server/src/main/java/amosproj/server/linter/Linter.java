package amosproj.server.linter;

import amosproj.server.data.*;
import amosproj.server.linter.checks.CheckBasics;
import amosproj.server.linter.checks.CheckGitlabFiles;
import amosproj.server.linter.checks.CheckGitlabSettings;
import amosproj.server.linter.utils.BeanUtil;

import java.time.LocalDateTime;

public class Linter {

    // entry point for api
    public LintingResult getResult(String repoUrl) {
        // get Objects
        Project lintingProject = getLintingProjectObject(repoUrl);
        LintingResult lintingResult = new LintingResult(lintingProject, LocalDateTime.now());
        // start linting
        checkEverything(lintingResult, lintingProject);

        return lintingResult;
    }

    private Project getLintingProjectObject(String url) {
        // Get Spring context to Access Database
        ProjectRepository projectRepository = BeanUtil.getBean(ProjectRepository.class);

        // save a project so i can get it (only while we dont have a real db)
        Project dummyProject = new Project("test", url);
        projectRepository.save(dummyProject);

        // get project from DB
        return projectRepository.findByUrl(url);
    }


    public void checkEverything(LintingResult lintingResult, Project project) {
        String URL = project.getUrl();

        // if not valid url --> mission abort
        if (!CheckBasics.isValidURL(URL)) {
            //todo: implement this?
            //removeResultFromDatabase(project); // do we need to do this or does api do this?
            return;
        }

        // get correct API URL
        String apiUrl;
        if (!hostedByGitlab(URL)) {
            return;
        }
        apiUrl = getApiUrlForGitlab(URL);

        // Actually start doing work with the api
        // Starting with Gitlab Settings Check
        SettingsCheck settingsCheck = new SettingsCheck(lintingResult, false);
        settingsCheck.setPublic(CheckGitlabSettings.isPublic(apiUrl));
        CheckGitlabFiles.checkMdFiles(apiUrl);

        // save checked data to db
        saveSettingsCheckObject(settingsCheck);
    }


    private static void saveSettingsCheckObject(SettingsCheck settingsCheck) {
        SettingsCheckRepository settingsCheckRepository = BeanUtil.getBean(SettingsCheckRepository.class);
        settingsCheckRepository.save(settingsCheck);
    }


    private String getApiUrlForGitlab(String url) {
        StringBuilder result = new StringBuilder();
        // Insert /api/v4 before the 3rd "/" (cause the first two are https://)
        // and encode the / in the url of the repository to %2F
        String[] parts = url.split("/");
        result = new StringBuilder("https://" + parts[2] + "/api/v4/projects/");
        for (int i = 3; i < parts.length - 1; i++) {
            result.append(parts[i]).append("%2F");
        }
        result.append(parts[parts.length - 1]);
        return result.toString();
    }

    private boolean hostedByGitlab(String url) {
        //check if url is gitlab.com
        String[] parts = url.split("/");
        return parts[2].matches("gitlab\\.com") || parts[2].matches("gitlab\\..*\\.com");
    }


}
