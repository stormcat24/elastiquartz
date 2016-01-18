package com.github.stormcat24.elastiquartz.publisher;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.stormcat24.elastiquartz.exception.FatalException;
import com.github.stormcat24.elastiquartz.exception.SystemException;
import com.github.stormcat24.elastiquartz.misc.InstancePool;
import com.github.stormcat24.elastiquartz.server.HealthCheckContext;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;

/**
 * @author stormcat24
 */
@Component("sqsMessagePublisher")
@Scope("prototype")
public class SqsMessagePublisher implements MessagePublisher {

    private AmazonSQSAsync sqs;

    private final ObjectMapper mapper = InstancePool.OBJECT_MAPPER;

    private static final Logger logger = LoggerFactory.getLogger(SqsMessagePublisher.class);

    @Autowired
    private HealthCheckContext healthCheckContext;

    @PostConstruct
    public void init() {
        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        AmazonSQSAsync sqs = new AmazonSQSAsyncClient(credentials);

        Optional<String> region = Optional.ofNullable(System.getenv("AWS_REGION"));
        region.ifPresent(r -> sqs.setRegion(Region.getRegion(Regions.fromName(r))));

        this.sqs = sqs;
    }

    @Override
    public void publish(String target, Map<Object, Object> message) {
        try {
            GetQueueUrlResult url = sqs.getQueueUrl(new GetQueueUrlRequest(target));
            sendMessage(url.getQueueUrl(), message);
            healthCheckContext.incrementSuccess();
        } catch (Exception e) {
            healthCheckContext.incrementError();
            logger.error(String.format("Failed to get queue url. QueueName=%s", target, e));
        }
    }

    private void sendMessage(String sqsUrl, Map<Object, Object> message) {

        String json = null;
        try {
            json = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new SystemException(e);
        }

        SendMessageRequest request = new SendMessageRequest(sqsUrl, json);

        try {
            sqs.sendMessage(request);
            healthCheckContext.incrementSuccess();
        } catch (Exception e) {
            healthCheckContext.incrementError();
            logger.error(String.format("Failed to send message. MessageBody=%s", request.getMessageBody(), e));
        }
    }

}
