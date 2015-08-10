package com.github.stormcat24.elastiquartz.schema;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author stormcat24
 */
@Component
public class CronDefinitionReader {

    private final Yaml yaml = new Yaml();

    public Map<String, List<CronDefinition>> read(InputStream is) {

        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String content = br.lines().map(s -> s + "\n").collect(Collectors.joining());
        return read(content);
    }

    @SuppressWarnings("unchecked")
    public Map<String, List<CronDefinition>> read(String content) {

        Object root = yaml.load(content);
        if (!(root instanceof Map)) {
            throw new RuntimeException("invalid value");
        }

        Map<String, List<CronDefinition>> cronDefMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) root).entrySet()) {
            String queueName = entry.getKey().toString();
            List<CronDefinition> cronDefItems = readList(entry.getValue());
            cronDefMap.put(queueName, cronDefItems);
        }
        return cronDefMap;
    }

    @SuppressWarnings("unchecked")
    private List<CronDefinition> readList(Object list) {

        if (!(list instanceof List)) {
            throw new RuntimeException("invalid value");
        }

        List<CronDefinition> cronDefItems = new ArrayList<>();

        if (list instanceof List) {
            for (Object entry : (List<Object>) list) {
                cronDefItems.add(readCronDefinition(entry));
            }
        }

        return cronDefItems;
    }

    @SuppressWarnings("unchecked")
    private CronDefinition readCronDefinition(Object value) {

        if (!(value instanceof Map)) {
            throw new RuntimeException("invalid value");
        }

        Map<Object, Object> map = (Map<Object, Object>) value;
        Object cron = map.get("cron");
        Object message = map.get("message");

        if (cron == null || StringUtils.isEmpty(cron.toString())) {
            throw new RuntimeException("TODO");
        }

        if (message == null || !(message instanceof Map)) {
            throw new RuntimeException("message is null of not map");
        }

        CronDefinition cronDef = new CronDefinition();
        cronDef.setCron(cron.toString());
        cronDef.setMessage((Map<Object, Object>) message);

        return cronDef;
    }

}
