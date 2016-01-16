package com.github.stormcat24.elastiquartz.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author stormcat24
 */
@RequiredArgsConstructor
public class OperationStatus {

    @Getter
    private final AtomicLong timerange;

    @Getter
    private AtomicInteger success = new AtomicInteger(0);

    @Getter
    private AtomicInteger error = new AtomicInteger(0);

    public int incrementSuccess() {
        return success.incrementAndGet();
    }

    public int incrementError() {
        return error.incrementAndGet();
    }
}
