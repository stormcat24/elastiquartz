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
    private String primaryHost;

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
        this.primaryHost = System.getenv("PRIMARY_HOST");
    }

    public boolean isStandbyServer() {
        return !StringUtils.isEmpty(primaryHost);
    }

}
