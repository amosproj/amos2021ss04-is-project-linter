package amosproj.server;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * GitLab ist dafür zuständig, dass die Verbindung zum Host, der in der config-Datei festgelegt wurde, erstellt wird.
 * Sie bietet das Interface zum GitLab4J Framework und der GitLab API selbst an.
 */
@Service
public class GitLab {

    private String apitoken;

    private String gitlabHost;

    private final org.gitlab4j.api.GitLabApi api;

    public GitLab(@Value("${GITLAB_ACCESS_TOKEN}") String token) {
        this.gitlabHost = Config.getConfigNode().get("settings").get("gitLabHost").asText();
        this.apitoken = token;
        this.api = new GitLabApi(gitlabHost, apitoken);
    }

    public GitLabApi getApi() {
        return api;
    }

    public String getGitlabHost() {
        return gitlabHost;
    }

    /**
     * Makes an authenticated request to the GitlabAPI. Commonly used for features GitLab4J doesn't support.
     *
     * @param resource the endpoint of the request i.e "/projects/123" will make an api call to :gitlabHost:/projects/123
     * @return Json of the result
     * @throws HttpStatusCodeException, JsonProcessingException, RestClientException
     */
    public JsonNode makeApiRequest(String resource) throws HttpStatusCodeException, JsonProcessingException, RestClientException {
        // prepare result storage
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder.build();
        // set auth header
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apitoken);
        final HttpEntity<String> entity = new HttpEntity<>(headers);
        // make request
        ResponseEntity<String> response = restTemplate.exchange(gitlabHost + "/api/v4" + resource, HttpMethod.GET, entity, String.class);
        // map to JSON and return
        ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
        JsonNode node = objectMapper.readTree(response.getBody());
        return node;
    }

}
