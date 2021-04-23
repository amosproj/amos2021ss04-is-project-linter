package amosproj.linter.server.linter;

import amosproj.linter.server.linter.consumptions.ProjectInformationJson;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class CheckGitlabSettings {

    public static boolean isPublic(String apiUrl) {
        // call api
        java.net.URI test;
        RestTemplate restTemplate = new RestTemplate();
        ProjectInformationJson info;
        try {
            test = new URI(apiUrl);
            info = restTemplate.getForObject(test, ProjectInformationJson.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        // if public return true
        return info != null && info.getVisibility() != null && info.getVisibility().equals("public");
    }
}
