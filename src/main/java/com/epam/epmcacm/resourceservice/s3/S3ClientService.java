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
import java.util.List;

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
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(this.accessKey, this.secretKey);
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(this.url))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

    }

    // Get the byte[] from this Amazon S3 object.
    public ResponseBytes<GetObjectResponse> getResourceFromStorage(String objectKey) throws ResourceNotFoundException {
        try {
            return this.s3Client.getObjectAsBytes(buildGetObjectRequest(objectKey));
        } catch (S3Exception e) {
            logger.error(e.awsErrorDetails().errorMessage());
            throw new ResourceNotFoundException("Resource does not exist with given id");
        }
    }

    public void createResourceInStorage(byte[] data, String objectKey) throws ResourceS3Exception {
        try {
            putObject(data,objectKey);
        } catch (S3Exception e) {
            logger.error(e.getMessage());
            throw new ResourceS3Exception("Error putting data into s3 local cloud storage simulator!");
        }
    }
    // Replaces the object in the bu

    public void deleteResourceFromStorage(List<Long> ids) throws ResourceS3Exception {
        try {
            ids.stream().map(id -> String.valueOf(id)).forEach(this::deleteObject);
        } catch (S3Exception e) {
            logger.error(e.getMessage());
            throw new ResourceS3Exception("Error deleting data from s3 local cloud storage simulator!");
        }
    }

    private GetObjectRequest buildGetObjectRequest(String objectKey) {
        return GetObjectRequest
                .builder()
                .key(objectKey)
                .bucket(S3_BUCKET_NAME)
                .build();
    }

    private void putObject(byte[] data, String objectKey) {
        this.s3Client.putObject(buildPutObjectRequest(objectKey),RequestBody.fromBytes(data));
    }

    private PutObjectRequest buildPutObjectRequest(String objectKey) {
        return PutObjectRequest.builder()
                .bucket(S3_BUCKET_NAME)
                .key(objectKey)
                .build();
    }

    private String deleteObject(String objectKey) {
        this.s3Client.deleteObject(buildDeleteObjectRequest(objectKey));
        return objectKey;
    }

    private DeleteObjectRequest buildDeleteObjectRequest(String objectKey) {
        return DeleteObjectRequest.builder()
                .bucket(S3_BUCKET_NAME)
                .key(objectKey)
                .build();
    }

}
