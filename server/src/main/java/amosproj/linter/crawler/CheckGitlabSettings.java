package amosproj.linter.crawler;

import amosproj.linter.server.data.LintingResults;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CheckGitlabSettings {
  public static boolean isPublic(LintingResults result, String apiUrl) {
    // any registered user has at least guest permission on public repos

    //create client
    var client = HttpClient.newHttpClient();
    //create request
    var request = HttpRequest
        .newBuilder(URI.create(apiUrl + "access_requests"))
        .header("accept", "application/json")
        .build();
    //send request via client
    HttpResponse<String> response;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      return false;
    }

    //check if permission is atleast 10 (guest) or higher
    return true;
  }
}
