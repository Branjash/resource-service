package com.epam.epmcacm.resourceservice.s3;

import org.springframework.beans.factory.annotation.Value;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.annotation.PostConstruct;
import java.net.URI;

public class S3ClientServiceTest {

    private static final String S3_BUCKET_NAME = "resource-service-bucket";
    private S3Client s3Client;

    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @Value("${amazonProperties.url}")
    private String url;

    @PostConstruct
    private void initClient() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(this.accessKey, this.secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(this.url))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}
