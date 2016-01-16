package com.github.stormcat24.elastiquartz.task;

import com.github.stormcat24.elastiquartz.config.Configuration;
import com.github.stormcat24.elastiquartz.exception.FatalException;
import com.github.stormcat24.elastiquartz.server.HealthCheckContext;
import com.github.stormcat24.elastiquartz.server.OperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author stormcat24
 */
@Component
public class HealthCheckTask {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckTask.class);

    @Autowired
    private HealthCheckContext healthCheckContext;

    @Autowired
    private Configuration configuration;

    // key 分, value
    @Scheduled(fixedRate = 60 * 1000)
    public void check() {

        int latestMinutes = configuration.getHealthCheckMinutes();

        List<OperationStatus> latest = healthCheckContext.getStatusItems().limit(latestMinutes).collect(Collectors.toList());

        int success = latest.stream().mapToInt(s -> s.getSuccess().get()).sum();
        int error = latest.stream().mapToInt(s -> s.getError().get()).sum();
        int total = success + error;

        logger.info("latest {} minutes status: success={}, error={}", latestMinutes, success, error);

        if (latest.size() == latestMinutes) {
            healthCheckContext.removeOldestEntry();
        }

        if (configuration.getFatalThresholdPercentage() > 0) {

            double errorPercentage = (double) error / (double) total * 100;
            logger.info("latest error percentage = {}%", (int) errorPercentage);
            if (errorPercentage >= configuration.getFatalThresholdPercentage()) {
                throw new FatalException(String.format("elastiquartz cannot continue. Error rate reached %s", (int) errorPercentage));
            }
        }
    }

}
