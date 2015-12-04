package com.github.stormcat24.elastiquartz.publisher;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(SqsMessagePublisher.class);

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

        sqs.getQueueUrlAsync(new GetQueueUrlRequest(target), new AsyncHandler<GetQueueUrlRequest, GetQueueUrlResult>() {
            @Override
            public void onError(Exception exception) {
                logger.error("Failed to get queue url. QueueName={}", target);
            }

            @Override
            public void onSuccess(GetQueueUrlRequest request, GetQueueUrlResult getQueueUrlResult) {
                sendMessage(getQueueUrlResult.getQueueUrl(), message);
            }
        });
    }

    private void sendMessage(String sqsUrl, Map<Object, Object> message) {

        String json = null;
        try {
            json = mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        SendMessageRequest request = new SendMessageRequest(sqsUrl, json);
        sqs.sendMessageAsync(request, new AsyncHandler<SendMessageRequest, SendMessageResult>() {
            @Override
            public void onError(Exception exception) {
                logger.error("Failed to send message. MessageBody={}", request.getMessageBody());
            }

            @Override
            public void onSuccess(SendMessageRequest request, SendMessageResult sendMessageResult) {
                logger.info("Sent messsage successfuly. MessageId={}, Message={}, Queue={}",
                        sendMessageResult.getMessageId(), request.getMessageBody(), request.getQueueUrl());
            }
        });
    }

}
