package amosproj.server.linter;

import amosproj.server.CachingService;
import amosproj.server.Config;
import amosproj.server.GitLab;
import amosproj.server.Scheduler;
import amosproj.server.api.ProjectController;
import amosproj.server.api.schemas.CrawlerStatusSchema;
import org.gitlab4j.api.GitLabApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class Crawler {

    protected static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    // autowired
    private final GitLab gitLab;
    private final Linter linter;
    private final CachingService cachingService;
    // end autowired

    private volatile AtomicBoolean crawlerActive;
    private volatile String progress;
    private volatile Long timeTaken;
    private volatile Long idx;
    private volatile String lastError;
    private volatile LocalDateTime errorTime;
    private volatile Long size;

    public Crawler(GitLab gitLab, Scheduler scheduler, Linter linter, CachingService cachingService) {
        // set autowired
        this.gitLab = gitLab;
        this.linter = linter;
        this.cachingService = cachingService;
        // init crawler status
        crawlerActive = new AtomicBoolean(false);
        progress = Config.getConfigNode().get("settings").get("crawler").get("status").get("inactive").asText();
        timeTaken = 0L;
        idx = 0L;
        lastError = "";
        errorTime = null;
        size = 0L;
        // init scheduling
        scheduler.scheduling(this::runCrawler);
    }

    /**
     * scheduled method that lints every repo in the instance at a specified cron time
     */
    @Async
    public synchronized void runCrawler() {
        crawlerActive.set(true);
        lastError = ""; // clear error message, but not error time as the maintainer might need to see what went wrong
        progress = Config.getConfigNode().get("settings").get("crawler").get("status").get("init").asText();
        logger.info(progress);
        try {
            int maxProjects = Config.getConfigNode().get("settings").get("crawler").get("maxProjects").asInt(Integer.MAX_VALUE);
            var projects = gitLab.getApi().getProjectApi().getProjects(50);
            size = Math.min((long) projects.getTotalItems(), maxProjects);
            progress = Config.getConfigNode().get("settings").get("crawler").get("status").get("active").asText();
            logger.info(progress);
            LocalDateTime start = LocalDateTime.now(Clock.systemUTC());

            int currentPage = 1;
            while (idx < maxProjects) {
                for (org.gitlab4j.api.models.Project proj : projects.page(currentPage)) {
                    logger.info("Linte Projekt " + proj.getWebUrl() + ", Fortschritt: " + ++idx + "/" + size);
                    linter.checkEverything(proj, start);
                    if (idx == maxProjects)
                        break;
                }
                currentPage++;
            }

            LocalDateTime end = LocalDateTime.now(Clock.systemUTC());
            timeTaken = ChronoUnit.SECONDS.between(start, end);
        } catch (GitLabApiException e) {
            lastError = e.getMessage();
            errorTime = LocalDateTime.now(Clock.systemUTC());
            e.printStackTrace();
        }
        progress = Config.getConfigNode().get("settings").get("crawler").get("status").get("inactive").asText();
        logger.info(progress);
        idx = 0L;
        cachingService.clearAllCaches(); // Clear all caches as they have inaccurate data in them
        crawlerActive.set(false);
    }

    public boolean getCrawlerActive() {
        return crawlerActive.get();
    }

    public CrawlerStatusSchema crawlerStatus() {
        return new CrawlerStatusSchema(progress, lastError, errorTime, crawlerActive.get(), size, idx, timeTaken);
    }
}
