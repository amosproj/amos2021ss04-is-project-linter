package amosproj.linter.crawler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CheckBasics {
  public static boolean isValidURL(String repoURL) {
    // make get request and check for error
    URL url = null;
    try {
      url = new URL(repoURL);
    } catch (MalformedURLException e) {
      return false;
    }

    HttpURLConnection con = null;
    try {
      con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    // todo: add something to do like return false when error
    return true;
  }
}
