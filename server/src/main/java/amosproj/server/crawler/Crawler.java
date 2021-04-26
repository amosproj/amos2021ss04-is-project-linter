package amosproj.server.crawler;

import amosproj.server.GitLab;
import amosproj.server.linter.Linter;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Crawler {

    @Autowired
    private GitLab api;

    @Autowired
    private Linter linter;


    public void runCrawler() {
        List<Project> projects = null;
        try {
            projects = api.getApi().getProjectApi().getProjects();
        } catch (GitLabApiException e) {
            e.printStackTrace();
        }

        for (Project proj : projects) {
            linter.checkEverything(proj);
        }

    }

}
