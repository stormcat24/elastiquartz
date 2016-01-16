package com.github.stormcat24.elastiquartz.quartz;

import com.github.stormcat24.elastiquartz.exception.SystemException;
import com.github.stormcat24.elastiquartz.publisher.MessagePublisher;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author stormcat24
 */
@Component
@Scope("prototype")
public class MessagePublishJob implements Job {

    @Autowired
    @Qualifier("messagePublisherFactory")
    private MessagePublisher messagePublisher;

    @Override
    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        Object target = dataMap.get("target");
        Object message = dataMap.get("message");

        if (target == null || !(target instanceof String)) {
            throw new SystemException(String.format("target is null or invalid structure"));
        }

        if (message == null || !(message instanceof Map)) {
            throw new SystemException(String.format("message is null or invalid structure"));
        }

        messagePublisher.publish((String) target, (Map<Object, Object>) message);
    }
}
