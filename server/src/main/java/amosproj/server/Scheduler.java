package amosproj.server;

import amosproj.server.linter.Linter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

@Service
public class Scheduler {
    @Autowired
    private TaskScheduler executor;

    public void scheduling(final Runnable task) {
        executor.schedule(task, new CronTrigger(Linter.getConfigNode().get("settings").get("crawler").get("scheduler").asText()));
    }
}
