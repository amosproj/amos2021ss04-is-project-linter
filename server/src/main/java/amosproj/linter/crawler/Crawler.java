package amosproj.linter.crawler;

import amosproj.linter.server.data.*;
import amosproj.linter.server.services.BeanUtil;

import java.time.LocalDateTime;


public class Crawler {
  // this function is only for testing purpouses
  public static void testCrawler() {
    getResult("https://gitlab.com/altaway/herbstluftwm");
  }

  // entry point for api
  public static LintingResult getResult(String repoUrl) {
    // get Objects
    Project lintingProject = getLintingProjectObject(repoUrl);
    LintingResult lintingResult = createNewLintingResultObject(lintingProject);

    // start linting
    checkEverything(lintingResult, lintingProject);

    return lintingResult;
  }

  private static Project getLintingProjectObject(String url) {
    ProjectRepository projectRepository = BeanUtil.getBean(ProjectRepository.class);
    // save a project so i can get it (only while we dont have a real db)
    Project dummyProject = new Project("test", url);
    projectRepository.save(dummyProject);

    // get project from DB
    return projectRepository.findByUrl(url);
  }

  private static LintingResult createNewLintingResultObject(Project lintingProject) {
    // method for better readability
    return new LintingResult(lintingProject, LocalDateTime.now());
  }

  public static void checkEverything(LintingResult lintingResult, Project project) {
    String URL = project.getUrl();

    // if not valid url --> mission abort
    if (!CheckBasics.isValidURL(URL)) {
      //todo: implement this?
      //removeResultFromDatabase(project); // do we need to do this or does api do this?
      return;
    }

    // get correct API URL
    String apiUrl;
    if (hostedByGitlab(URL)) {
      // hosted by gitlab.com
      apiUrl = getApiUrlForGitlabDotComProject(URL);
    } else {
      // hosted by gitlab.example.com (gitlab instance)
      apiUrl = getApiUrlForGitlabInstanceProject(URL);
    }

    // Actually start doing work
    // Starting with Gitlab Settings Check
    SettingsCheck settingsCheck = createSettingsCheckObject(lintingResult);
    settingsCheck.setPublic(CheckGitlabSettings.isPublic(apiUrl));

    // save checked data to db
    saveSettingsCheckObject(settingsCheck);
  }

  private static void saveSettingsCheckObject(SettingsCheck settingsCheck) {
    SettingsCheckRepository settingsCheckRepository = BeanUtil.getBean(SettingsCheckRepository.class);
    settingsCheckRepository.save(settingsCheck);
  }

  private static SettingsCheck createSettingsCheckObject(LintingResult lintingResult) {
    return new SettingsCheck(lintingResult, false, false, false, false, false);
  }

  private static String getApiUrlForGitlabInstanceProject(String url) {
    // Insert /api/v4 before the 3rd "/" (cause the first two are https://)
    String[] parts = url.split("/");
    String[] newParts = new String[parts.length + 1];
    int j = 0;
    for (int i = 0; i < parts.length; i++) {
      newParts[j] = parts[i];
      if (i == 2) {
        newParts[j + 1] = "api/v4";
        j++;
      }
      j++;
    }

    StringBuilder sb = new StringBuilder();
    for (String part :
        newParts) {
      sb.append(part);
      sb.append('/');
    }

    return sb.toString();
  }

  private static boolean hostedByGitlab(String url) {
    // check if url is gitlab.com or gitlab.example.com
    String[] parts = url.split("\\.");
    // return true if gitlab.com, false if not
    if (parts[0].equals("https://gitlab")) return true;
    return parts[1].equals("gitlab");
  }

  private static String getApiUrlForGitlabDotComProject(String URL) {
    //todo implement this, ONLY GIVES BACK DUMMY ANSWER
//    // example ID  URL?: https://gitlab.com/api/v4/projects/26063188
//    long id = getGitlabDotComProjectId(URL); // i dont know how to do this right now?!
//    return "https://gitlab.com/api/v4/projects/" + id + "/";
    return "https://gitlab.com/api/v4/projects/26063188/";
  }
}

