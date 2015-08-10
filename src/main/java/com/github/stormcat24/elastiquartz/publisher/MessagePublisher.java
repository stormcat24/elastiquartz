package com.github.stormcat24.elastiquartz.publisher;

import java.util.Map;

/**
 * @author stormcat24
 */
public interface MessagePublisher {

    void publish(String target, Map<Object, Object> message);

}
