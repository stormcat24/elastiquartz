package com.github.stormcat24.elastiquartz.provider;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.github.stormcat24.elastiquartz.config.Configuration;
import com.github.stormcat24.elastiquartz.schema.CronDefinition;
import com.github.stormcat24.elastiquartz.schema.CronDefinitionReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author stormcat24
 */
@Component("s3Provider")
public class S3Provider implements CronProvider {

    private AmazonS3 s3;

    @Autowired
    private CronDefinitionReader reader;

    @Autowired
    private Configuration configuration;

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
        S3Object s3Obj = s3.getObject(bucketName, String.format("%s.yml", configuration.getCronTarget()));

        return reader.read(s3Obj.getObjectContent());
    }
}
