package amosproj.linter.server.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Scheduled(fixedRate = 5000) // every 5 sec // TODO use cron to make daily task
    public void dailyLint() {
        System.out.println("hallo");
    }
}
