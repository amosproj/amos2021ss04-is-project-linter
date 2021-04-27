package amosproj.server.linter.checks;

import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class CheckGitlabFiles {

    public static void checkMdFiles(String apiUrl){
        //TODO: insert the results into the DB

        existsMdFile(apiUrl, "README.md");
        existsMdFile(apiUrl, "CONTRIBUTING.md");
        if(existsMdFile(apiUrl, "MAINTAINERS.md")){
            List<String> maintainers = extractMaintainers(apiUrl);
        }
    }

    private static boolean existsMdFile(String url, String filePath){
        // call api
        String apiUrl = "";
        try {
            apiUrl = url + "/repository/files/" + URLEncoder.encode(filePath, "US-ASCII") + "?ref=master";
            System.out.println(apiUrl);
        } catch (java.io.UnsupportedEncodingException e){
            return false;
        }
        java.net.URI test;
        RestTemplate restTemplate = new RestTemplate();
        String info;
        try {
            test = new URI(apiUrl);
            info = restTemplate.getForObject(test, String.class);
            System.out.println(info);
        } catch (Exception e) {
            return  false;
        }
        return true;
    }

    private static List<String> extractMaintainers(String url){
        List<String> maintainers = new ArrayList<>();
        String apiUrl = url + "/repository/files/MAINTAINERS%2Emd/raw?ref=master";
        java.net.URI test;
        RestTemplate restTemplate = new RestTemplate();
        String info;
        try {
            test = new URI(apiUrl);
            info = restTemplate.getForObject(test, String.class);
            //read maintainers out of raw
        } catch (Exception e) {
        }
        return maintainers;
    }
}
