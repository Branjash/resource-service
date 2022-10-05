package com.epam.epmcacm.resourceservice.s3;


import com.epam.epmcacm.resourceservice.exceptions.ResourceNotFoundException;
import com.epam.epmcacm.resourceservice.exceptions.ResourceS3Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.annotation.PostConstruct;
import java.net.URI;

@Component
public class S3ClientService {

    private static final Logger logger = LoggerFactory.getLogger(S3ClientService.class);
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
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(this.accessKey, this.secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(url))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

    }

    // Get the byte[] from this Amazon S3 object.
    public byte[] getObjectBytes(String keyName) throws ResourceNotFoundException {
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(keyName)
                    .bucket(S3_BUCKET_NAME)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = this.s3Client.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();
            return data;

        } catch (S3Exception e) {
            logger.error(e.awsErrorDetails().errorMessage());
            throw new ResourceNotFoundException("Resource doesn’t exist with given id");
        }
    }

    // Places an image into a S3 bucket.
    public String putObject(byte[] data, String objectKey) throws ResourceS3Exception {

        try {
            PutObjectResponse response = this.s3Client.putObject(PutObjectRequest.builder()
                            .bucket(S3_BUCKET_NAME)
                            .key(objectKey)
                            .build(),
                    RequestBody.fromBytes(data));

            return response.eTag();

        } catch (S3Exception e) {
            logger.error(e.getMessage());
            throw new ResourceS3Exception("Error putting data into s3 local cloud storage simulator!");
        }
    }

    public String deleteObject(String objectKey) throws ResourceS3Exception {

        try {
            DeleteObjectResponse response = this.s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(S3_BUCKET_NAME)
                    .key(objectKey)
                    .build());

            return objectKey;

        } catch (S3Exception e) {
            logger.error(e.getMessage());
            throw new ResourceS3Exception("Error deleting data from s3 local cloud storage simulator!");
        }
    }


}
