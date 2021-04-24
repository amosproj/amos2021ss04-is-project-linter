package amosproj.server.jobs;

import amosproj.server.crawler.Crawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Scheduled(cron =  "0 0 * * * ?") // every 24 hours at midnight
    public void dailyCrawl() {
    }

}
