package com.commit.weatherAlarm.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;


@Configuration
public class LambdaConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.credentials.access-key}")
    private String accessKeyId;

    @Value("${aws.credentials.secret-key}")
    private String secretAccessKey;

    @Bean
    public LambdaClient lambdaClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        return LambdaClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
}
