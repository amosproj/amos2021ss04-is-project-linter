package amosproj.linter.crawler;

import amosproj.linter.server.data.LintingResults;
import amosproj.linter.server.data.Project;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.time.LocalDateTime;

public class Crawler {
  // this main is only for testing purpouses
  public static void main(String[] args) {
    getResult("https://gitlab.com/altaway/herbstluftwm");
  }

  // entry point for api
  public static LintingResults getResult(String repoUrl) {
    LintingResults result = getResultObject(repoUrl);
//    result = checkEverything(result);
    return result;
  }

  private static LintingResults getResultObject(String repoUrl) {
    //todo implement this, ONLY RETURNS FAKE RIGHT NOW
    return new LintingResults(LocalDateTime.now(), new Project("test", "localhost:1337"));
  }

//  public static LintingResults checkEverything(LintingResults result) {
//    String URL = result.getRepoLink();
//
//    // if not valid url --> mission abort
//    if (!CheckBasics.isValidURL(URL)) {
//      //todo: implement this?
//      // removeResultFromDatabase(result); // do we need to do this or does api do this?
//      return result;
//    }
//
//    // get correct API URL
//    String apiUrl;
//    if (hostedByGitlab(URL)) {
//      // hosted by gitlab.com
//      apiUrl = getApiUrlForGitlabDotComProject(URL);
//    } else {
//      // hosted by gitlab.example.com (gitlab instance)
//      apiUrl = getApiUrlForGitlabInstanceProject(URL);
//    }
//    result.setApiLink(apiUrl);
//
//    // actually start doing work with the api
//    result.setPublic(CheckGitlabSettings.isPublic(result.getApiLink()));
//
//    return result;
//  }

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

