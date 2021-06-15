package amosproj.server.linter;

import amosproj.server.Config;
import amosproj.server.GitLab;
import amosproj.server.Scheduler;
import amosproj.server.api.schemas.CrawlerStatusSchema;
import org.gitlab4j.api.GitLabApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class Crawler {

    protected static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    // autowired
    private final GitLab gitLab;
    private final Linter linter;
    // end autowired

    private AtomicBoolean crawlerActive;
    private String progress;
    private Long timeTaken;
    private Long idx;
    private String lastError;
    private LocalDateTime errorTime;
    private Long size;

    public Crawler(GitLab gitLab, Scheduler scheduler, Linter linter) {
        // set autowired
        this.gitLab = gitLab;
        this.linter = linter;
        // init crawler status
        crawlerActive = new AtomicBoolean(false);
        progress = Config.getConfigNode().get("settings").get("crawler").get("status").get("inactive").asText();
        timeTaken = 0L;
        idx = 0L;
        lastError = "";
        errorTime = null;
        size = 0L;
        // init scheduling
        scheduler.scheduling(this::startCrawler);
    }

    /**
     * scheduled method that lints every repo in the instance at a specified cron time
     */
    private synchronized void runCrawler() {
        crawlerActive = new AtomicBoolean(true);
        progress = Config.getConfigNode().get("settings").get("crawler").get("status").get("init").asText();
        logger.info(progress);
        try {
            var projects = gitLab.getApi().getProjectApi().getProjects(0, Config.getConfigNode().get("settings").get("crawler").get("maxProjects").asInt(Integer.MAX_VALUE));
            size = (long) projects.size();
            progress = Config.getConfigNode().get("settings").get("crawler").get("status").get("active").asText();
            logger.info(progress);
            LocalDateTime start = LocalDateTime.now();

            for (org.gitlab4j.api.models.Project proj : projects) {
                logger.info("Linte Projekt " + proj.getWebUrl() + ", Fortschritt: " + ++idx + "/" + size);
                linter.checkEverything(proj, start);
            }

            LocalDateTime end = LocalDateTime.now();
            timeTaken = ChronoUnit.SECONDS.between(start, end);
        } catch (GitLabApiException e) {
            lastError = e.getMessage();
            errorTime = LocalDateTime.now();
            e.printStackTrace();
        }
        progress = Config.getConfigNode().get("settings").get("crawler").get("status").get("inactive").asText();
        logger.info(progress);
        crawlerActive.set(false);
        idx = 0L;
    }

    /**
     * Used to stop post-spamming of the API endpoint
     *
     * @return Boolean, ob der crawler gestartet wurde oder nicht
     */
    public boolean startCrawler() {
        if (!crawlerActive.get()) {// Only one crawler should run at any given time
            Thread thread = new Thread(this::runCrawler);
            thread.start();
            return true;
        } else {
            return false;
        }

    }

    public CrawlerStatusSchema crawlerStatus() {
        return new CrawlerStatusSchema(progress, lastError, errorTime, crawlerActive.get(), size, idx, timeTaken);
    }
}
