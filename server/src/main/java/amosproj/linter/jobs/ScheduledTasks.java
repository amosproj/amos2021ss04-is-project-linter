package amosproj.linter.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Scheduled(cron = "0 0 * * *")
    public void dailyLint() {
        // TODO start lint job her
        System.out.println("time");
    }
}
