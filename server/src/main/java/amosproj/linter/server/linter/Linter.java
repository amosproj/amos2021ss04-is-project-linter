package amosproj.linter.server.linter;

import amosproj.linter.server.data.LintingResult;
import amosproj.linter.server.data.Project;
import amosproj.linter.server.data.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;


public class Linter {

    @Autowired
    private ProjectRepository projectRepository;

    // entry point for api
    public LintingResult getResult(String repoUrl) {
        // get Objects
        Project lintingProject = getLintingProjectObject(repoUrl);
        LintingResult lintingResult = createNewLintingResultObject(lintingProject);
        // start linting
        checkEverything(lintingResult, lintingProject);

        return lintingResult;
    }

    private Project getLintingProjectObject(String url) {
        // save a project so i can get it (only while we dont have a real db)
        Project dummyProject = new Project("test", url);
        projectRepository.save(dummyProject);

        // get project from DB
        return projectRepository.findByUrl(url);
    }

    private LintingResult createNewLintingResultObject(Project lintingProject) {
        // method for better readability
        return new LintingResult(lintingProject, LocalDateTime.now());
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
        // todo: waiting for DB Team to implement SettingsCheck Table so we can save it to a settings Object
        // will be something like:
//    SettingsCheck settingsCheck = createSettingsCheckObject(lintingResult);
//    lintingGitlabSettings.setPublic(CheckGitlabSettings.isPublic(apiUrl));
        CheckGitlabSettings.isPublic(apiUrl);
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

//  private String getApiUrlForGitlabInstanceProject(String url) {
//    // Insert /api/v4 before the 3rd "/" (cause the first two are https://)
//    String[] parts = url.split("/");
//    String[] newParts = new String[parts.length + 1];
//    int j = 0;
//    for (int i = 0; i < parts.length; i++) {
//      newParts[j] = parts[i];
//      if (i == 2) {
//        newParts[j + 1] = "api/v4";
//        j++;
//      }
//      j++;
//    }
//
//    StringBuilder sb = new StringBuilder();
//    for (String part :
//        newParts) {
//      sb.append(part);
//      sb.append('/');
//    }
//
//    return sb.toString();
//  }

    private boolean hostedByGitlab(String url) {
        //check if url is gitlab.com
        String[] parts = url.split("/");
        return parts[2].matches("gitlab\\.com") || parts[2].matches("gitlab\\..*\\.com");
        // check if url is gitlab.com or gitlab.example.com
        //String[] parts = url.split("\\.");
        // return true if gitlab.com, false if not
//    if (parts[0].equals("https://gitlab")) return true;
//    return parts[1].equals("gitlab");
    }

//  private static String getApiUrlForGitlabDotComProject(String URL) {
//    System.out.println(URL);
//
//    //todo implement this, ONLY GIVES BACK DUMMY ANSWER
//
////    // example ID  URL?: https://gitlab.com/api/v4/projects/26063188
////    long id = getGitlabDotComProjectId(URL); // i dont know how to do this right now?!
////    return "https://gitlab.com/api/v4/projects/" + id + "/";
// //   return "https://gitlab.com/api/v4/projects/26063188?access_token=dvmC-3KRRLsSXRbH63r";
//    return "https://gitlab.com/api/v4/projects/kalilinux%2Fpackages%2Ftyper";
// //   return "https://gitlab.com/api/v4/projects/26063188/";
//  }

}
