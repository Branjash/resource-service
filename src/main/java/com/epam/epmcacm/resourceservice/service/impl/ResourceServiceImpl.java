package com.epam.epmcacm.resourceservice.service.impl;

import com.epam.epmcacm.resourceservice.rest.ResourceRepository;
import com.epam.epmcacm.resourceservice.service.api.ResourceService;
import com.epam.epmcacm.resourceservice.util.ResourceUtility;
import com.epam.epmcacm.resourceservice.exceptions.ResourceS3Exception;
import com.epam.epmcacm.resourceservice.model.Resource;
import com.epam.epmcacm.resourceservice.s3.S3ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.IOException;

import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

    @Value("${resource.kafka.topic}")
    private String topic;

    private final KafkaTemplate kafka;
    private final ResourceRepository resourceRepository;
    private final S3ClientService s3Client;
    public ResourceServiceImpl(ResourceRepository resourceRepository, S3ClientService s3Client, KafkaTemplate kafkaTemplate) {
        this.resourceRepository = resourceRepository;
        this.s3Client = s3Client;
        this.kafka = kafkaTemplate;
    }

    @Override
    public Resource saveResource(MultipartFile file) throws ResourceS3Exception, IOException {
        Resource resource = new Resource(file.getOriginalFilename());
        resource = resourceRepository.save(resource);
        s3Client.createOrUpdateResourceInStorage(file.getBytes(),String.valueOf(resource.getId()));
        logger.info("Successfully save resource data: {}", resource);
        return resource;
    }

    @Override
    public Resource sendResourceKafkaEvent(Resource resource) {
        String resourceId = String.valueOf(resource.getId());
        kafka.send(topic, resourceId, resource);
        logger.info("Successfully sent kafka event with key: {} of resource upload finish", resourceId);
        return resource;
    }

    @Override
    public Resource updateResource(Long id, MultipartFile file) throws ResourceNotFoundException, IOException, ResourceS3Exception {
        Resource currentResource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource does not exist with given id!"));
        currentResource.setName(file.getOriginalFilename());
        currentResource = resourceRepository.save(currentResource);
        s3Client.createOrUpdateResourceInStorage(file.getBytes(), String.valueOf(currentResource.getId()));
        logger.info("Successfully updated resource: {}", currentResource);
        return currentResource;
    }

    @Override
    public ResponseEntity<ByteArrayResource> getResourceById(Long id) throws ResourceNotFoundException {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource does not exist with given id!"));
        ResponseBytes<GetObjectResponse> s3ResourceBytes = s3Client.getResourceFromStorage(String.valueOf(resource.getId()));
        logger.info("Successfully found resource with id {}", id);
        return ResourceUtility.createResponseForGetResource(s3ResourceBytes,resource);
    }

    @Override
    public void deleteResources(List<Long> ids) throws ResourceS3Exception {
        resourceRepository.deleteAllById(ids);
        s3Client.deleteResourcesFromStorage(ids);
        logger.info("Successfully deleted resources with ids: {}", ids);
    }


}
