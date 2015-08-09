package com.github.stormcat24.elastiquartz.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author stormcat24
 */
@Component
public class CronCheckTask {

    @Scheduled(fixedRate = 60 * 1000)
    public void check() {
        System.out.println("nekotan!!");
    }
}
