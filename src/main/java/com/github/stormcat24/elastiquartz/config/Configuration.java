package com.github.stormcat24.elastiquartz.config;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author stormcat24
 */
@Component
public class Configuration {

    @Getter
    private String cronLocationType;

    @Getter
    private String cronLocation;

    @Getter
    private String cronTarget;

    @Getter
    private String eventTargetType;

    @Getter
    private int healthCheckMinutes = 5;

    @Getter
    private int fatalThresholdPercentage = -1;

    @PostConstruct
    public void init() {
        String cronLocationType = System.getenv("CRON_LOCATION_TYPE");
        if (StringUtils.isEmpty(cronLocationType)) {
            cronLocationType = "s3";
        }
        this.cronLocationType = cronLocationType;

        this.cronLocation = System.getenv("CRON_LOCATION");
        this.cronTarget = System.getenv("CRON_TARGET");
        this.eventTargetType = System.getenv("EVENT_TARGET_TYPE");

        String rawHealthCheckMinutes = System.getenv("HEALTH_CHECK_MINUTES");
        if (!StringUtils.isEmpty(rawHealthCheckMinutes)) {
            int value = Integer.parseInt(rawHealthCheckMinutes);
            if (value <= 0) {
                healthCheckMinutes = 1;
            } else {
                healthCheckMinutes = value;
            }
        }

        String rawFatalThresholdPercentage = System.getenv("FATAL_THRESHOLD_PERCENTAGE");
        if (!StringUtils.isEmpty(rawFatalThresholdPercentage)) {
            int value = Integer.parseInt(rawFatalThresholdPercentage);
            if (value >= 100) {
                fatalThresholdPercentage = 100;
            } else {
                fatalThresholdPercentage = value;
            }
        }
    }

}
