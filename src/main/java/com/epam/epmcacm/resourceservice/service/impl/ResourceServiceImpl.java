package com.epam.epmcacm.resourceservice.service.impl;

import com.epam.epmcacm.resourceservice.exceptions.ResourceS3Exception;
import com.epam.epmcacm.resourceservice.model.Resource;
import com.epam.epmcacm.resourceservice.rest.ResourceRepository;
import com.epam.epmcacm.resourceservice.rest.StorageServiceRestClient;
import com.epam.epmcacm.resourceservice.service.api.ResourceService;
import com.epam.epmcacm.resourceservice.util.ResourceUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

    @Value("${resource.kafka.topic}")
    private String topic;

    private final KafkaTemplate kafka;
    private final ResourceRepository resourceRepository;
    private final StorageServiceRestClient storageService;

    public ResourceServiceImpl(KafkaTemplate kafka, ResourceRepository resourceRepository, StorageServiceRestClient storageService) {
        this.kafka = kafka;
        this.resourceRepository = resourceRepository;
        this.storageService = storageService;
    }

    @Override
    public Resource saveResource(MultipartFile file) throws IOException, ResourceS3Exception {
        Resource resource = new Resource(file.getOriginalFilename());
        Long storageId = ResourceUtility.saveInStorageAndGetId(file, f -> storageService.createResourceInStorage(f));
        resource.setStorageId(storageId);
        resource = resourceRepository.save(resource);
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
    public Resource updateResource(Long id, MultipartFile file) throws ResourceNotFoundException, IOException {
        Resource currentResource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource does not exist with given id!"));
        currentResource.setName(file.getOriginalFilename());
        currentResource = resourceRepository.save(currentResource);
        logger.info("Successfully updated resource: {}", currentResource);
        return currentResource;
    }

    @Override
    public ResponseEntity<ByteArrayResource> getResourceById(Long id) throws ResourceNotFoundException {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource does not exist with given id!"));
        ResponseEntity<ByteArrayResource> s3ResourceBytes = storageService.getResourceFromStorage(resource.getId());
        if(s3ResourceBytes.getStatusCode() != HttpStatus.OK) throw new ResourceNotFoundException("Resource does not exist in storage with given id!");
        logger.info("Successfully found resource with id {}", id);
        return s3ResourceBytes;
    }

    @Override
    public void deleteResources(List<Long> ids) {
        resourceRepository.deleteAllById(ids);
        storageService.deleteResourcesFromStorage(ids);
        logger.info("Successfully deleted resources with ids: {}", ids);
    }


}
