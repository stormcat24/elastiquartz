package com.github.stormcat24.elastiquartz.schema;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author stormcat24
 */
public class CronDefinition {

    @Getter @Setter
    private String cron;

    @Getter @Setter
    private Map<Object, Object> message;

}
