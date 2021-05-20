package amosproj.server;

import amosproj.server.linter.Linter;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * GitLab ist dafür zuständig, dass die Verbindung zum Host, der in der config-Datei festgelegt wurde, erstellt wird.
 * Sie bietet das Interface zum GitLab4J Framework und der GitLab API selbst an.
 */
@Service
public class GitLab {

    private String apitoken;

    private String gitlabHost = Linter.getConfigNode().get("settings").get("gitLabHost").asText();

    private final org.gitlab4j.api.GitLabApi api;

    public GitLab(@Value("${GITLAB_ACCESS_TOKEN}") String token) {
        this.apitoken = token;
        api = new GitLabApi(gitlabHost, apitoken);
    }

    public GitLabApi getApi() {
        return api;
    }

    public String getGitlabHost() {
        return gitlabHost;
    }

    public JsonNode makeApiRequest(String url) throws JsonProcessingException {
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder.build();
        String body = restTemplate.getForObject(url, String.class);
        ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
        JsonNode node = objectMapper.readTree(body);
        return node;
    }

}
