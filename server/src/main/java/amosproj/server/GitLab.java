package amosproj.server;

import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GitLab {

    @Value("${GITLAB_ACCESS_TOKEN}")
    private String apitoken;

    private final org.gitlab4j.api.GitLabApi api;

    public GitLab() {
        api = new GitLabApi("https://gitlab.com", apitoken);
    }

    public GitLabApi getApi() {
        return api;
    }
}
