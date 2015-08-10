package com.github.stormcat24.elastiquartz.provider;

import com.github.stormcat24.elastiquartz.schema.CronDefinition;

import java.util.List;
import java.util.Map;

/**
 * @author stormcat24
 */
public interface CronProvider {

    Map<String, List<CronDefinition>> getCronDefinitionMap();

}
