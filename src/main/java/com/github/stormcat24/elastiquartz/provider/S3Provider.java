package com.github.stormcat24.elastiquartz.provider;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.github.stormcat24.elastiquartz.config.Configuration;
import com.github.stormcat24.elastiquartz.exception.SystemException;
import com.github.stormcat24.elastiquartz.schema.CronDefinition;
import com.github.stormcat24.elastiquartz.schema.CronDefinitionReader;
import com.github.stormcat24.elastiquartz.server.HealthCheckContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author stormcat24
 */
@Component("s3Provider")
@Scope("prototype")
public class S3Provider implements CronProvider {

    private AmazonS3 s3;

    @Autowired
    private CronDefinitionReader reader;

    @Autowired
    private Configuration configuration;

    @Autowired
    private HealthCheckContext healthCheckContext;

    @PostConstruct
    public void init() {

        AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();
        AmazonS3 s3 = new AmazonS3Client(credentials);

        Optional<String> region = Optional.ofNullable(System.getenv("AWS_REGION"));
        region.ifPresent(r -> s3.setRegion(Region.getRegion(Regions.fromName(r))));

        this.s3 = s3;
    }

    @Override
    public Map<String, List<CronDefinition>> getCronDefinitionMap() {

        String bucketName = configuration.getCronLocation();

        try {
            S3Object s3Obj = s3.getObject(bucketName, String.format("%s.yml", configuration.getCronTarget()));
            return reader.read(s3Obj.getObjectContent());
        } catch (Exception e) {
            healthCheckContext.incrementError();
            throw new SystemException(e);
        } finally {
            healthCheckContext.incrementSuccess();
        }

    }
}
