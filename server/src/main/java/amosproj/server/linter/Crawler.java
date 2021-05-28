package amosproj.server.linter;

import amosproj.server.GitLab;
import amosproj.server.Scheduler;
import amosproj.server.api.schemas.CrawlerStatusSchema;
import org.gitlab4j.api.GitLabApiException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class Crawler {

    // autowired
    private final GitLab gitLab;
    private final Scheduler scheduler;
    private final Linter linter;
    // end autowired

    private boolean crawlerActive;
    private String progress;
    private Long timeTaken;
    private Long idx = 0L;
    private String lastError;
    private LocalDateTime errorTime;
    private Long size;

    public Crawler(GitLab gitLab, Scheduler scheduler, Linter linter) {
        this.gitLab = gitLab;
        this.scheduler = scheduler;
        this.linter = linter;
        // init scheduling
        scheduler.scheduling(this::runCrawler);
    }

    /**
     * scheduled method that lints every repo in the instance at a specified cron time
     */
    public void runCrawler() {
        crawlerActive = true;
        progress = "Starting crawler and getting all Projects";
        try {
            var projects = gitLab.getApi().getProjectApi().getProjects();
            size = Long.valueOf(projects.size());
            progress = "Linting the projects";
            long start = System.currentTimeMillis();

            for (org.gitlab4j.api.models.Project proj : projects) {
                linter.checkEverything(proj);
                idx++;
            }

            long end = System.currentTimeMillis();
            timeTaken = (end-start);
        } catch (GitLabApiException e) {
            lastError = e.getMessage();
            errorTime = LocalDateTime.now();
            e.printStackTrace();
        }
        progress = "Crawling process finished!";
        crawlerActive = false;
        idx = 0L;
    }

    public CrawlerStatusSchema crawlerStatus() {
        return new CrawlerStatusSchema(progress, lastError, errorTime, crawlerActive, size, idx, timeTaken);
    }
}
