package com.github.stormcat24.elastiquartz.publisher;

import com.github.stormcat24.elastiquartz.config.Configuration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author stormcat24
 */
@Component
public class MessagePublisherFactory implements FactoryBean<MessagePublisher> {

    @Autowired
    private Configuration configuration;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public MessagePublisher getObject() throws Exception {
        String eventTargetType = configuration.getEventTargetType();
        if (StringUtils.isEmpty(eventTargetType)) {
            return null;
        } else if ("sqs".equals(eventTargetType)) {
            return (MessagePublisher) applicationContext.getBean("sqsMessagePublisher");
        }
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return MessagePublisher.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
