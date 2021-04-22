package amosproj.linter.crawler;

import amosproj.linter.crawler.consumptions.ProjectInformationJson;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class CheckGitlabSettings {

  public static boolean isPublic(String apiUrl) {
    // call api
    RestTemplate restTemplate = new RestTemplate();
    ProjectInformationJson info;
    try {
      info = restTemplate.getForObject(apiUrl, ProjectInformationJson.class);
    } catch (HttpClientErrorException e) {
      return false;
    }
    // if public return true
    return info != null && info.getVisibility().equals("public");
  }
}
