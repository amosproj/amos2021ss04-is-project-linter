package amosproj.server;

import org.gitlab4j.api.GitLabApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GitLab {

    private String apitoken;

    private String gitlabHost = "https://gitlab.cs.fau.de";

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
}
