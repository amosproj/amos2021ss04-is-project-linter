package amosproj.server.linter;

import amosproj.server.api.CachingService;
import amosproj.server.Config;
import amosproj.server.GitLab;
import amosproj.server.Scheduler;
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
    private volatile String status;
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
        status = Config.getConfigNode().get("settings").get("crawler").get("status").get("inactive").asText();
        timeTaken = 0L;
        idx = 0L;
        lastError = "";
        errorTime = null;
        size = 0L;
        // init scheduling
        scheduler.scheduling(this::runCrawler);
        // populate caches on startup
        cachingService.repopulateCaches();
    }

    /**
     * scheduled method that lints every repo in the instance at a specified cron time
     */
    @Async
    public synchronized void runCrawler() {
        crawlerActive.set(true);
        lastError = ""; // clear error message, but not error time as the maintainer might need to see what went wrong
        status = Config.getConfigNode().get("settings").get("crawler").get("status").get("init").asText();
        logger.info(status);
        try {
            int maxProjects = Config.getConfigNode().get("settings").get("crawler").get("maxProjects").asInt(Integer.MAX_VALUE);
            var projects = gitLab.getApi().getProjectApi().getProjects(50);
            size = Math.min((long) projects.getTotalItems(), maxProjects);
            status = Config.getConfigNode().get("settings").get("crawler").get("status").get("active").asText();
            logger.info(status);
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
        idx = 0L;
        status = Config.getConfigNode().get("settings").get("crawler").get("status").get("cache").asText();
        logger.info(status);
        cachingService.repopulateCaches(); // reload caches on the server side, so it's always instant for the end user
        status = Config.getConfigNode().get("settings").get("crawler").get("status").get("inactive").asText();
        logger.info(status);
        crawlerActive.set(false);
    }

    public boolean getCrawlerActive() {
        return crawlerActive.get();
    }

    public CrawlerStatusSchema crawlerStatus() {
        return new CrawlerStatusSchema(status, lastError, errorTime, crawlerActive.get(), size, idx, timeTaken);
    }
}
