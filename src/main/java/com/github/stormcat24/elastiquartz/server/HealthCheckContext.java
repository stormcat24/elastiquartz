package com.github.stormcat24.elastiquartz.server;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * @author stormcat24
 */
@Component
public class HealthCheckContext {

    private Map<AtomicLong, OperationStatus> map = new LinkedHashMap<>();

    private AtomicLong currentTimerange;

    @PostConstruct
    public void init() {
        currentTimerange = new AtomicLong(System.currentTimeMillis());
    }

    public int incrementSuccess() {
        OperationStatus status = getStatus();
        return status.incrementSuccess();
    }

    public int incrementError() {
        OperationStatus status = getStatus();
        return status.incrementError();
    }

    public OperationStatus getStatus() {

        long now = System.currentTimeMillis();
        long sub = now - currentTimerange.get();

        if (sub > 1000 * 60) {
            currentTimerange = new AtomicLong(now);
        }

        OperationStatus status = map.get(currentTimerange);
        if (status == null) {
            status = new OperationStatus(currentTimerange);
            map.put(currentTimerange, status);
        }
        return status;
    }

    public Stream<OperationStatus> getStatusItems() {
        return map.entrySet().stream().map(s -> s.getValue());
    }

    public void removeOldestEntry() {
        Optional<Map.Entry<AtomicLong, OperationStatus>> head = map.entrySet().stream().findFirst();
        if (head.isPresent()) {
            map.remove(head.get());
        }
    }
}
