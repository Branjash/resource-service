package com.epam.epmcacm.resourceservice;

import com.epam.epmcacm.resourceservice.exceptions.ResourceNotFoundException;
import com.epam.epmcacm.resourceservice.exceptions.ResourceS3Exception;
import com.epam.epmcacm.resourceservice.model.Resource;
import com.epam.epmcacm.resourceservice.s3.S3ClientService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import java.io.IOException;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final S3ClientService s3Client;
    public ResourceService(ResourceRepository resourceRepository, S3ClientService s3Client) {
        this.resourceRepository = resourceRepository;
        this.s3Client = s3Client;
    }

    public Resource saveResource(MultipartFile file) throws ResourceS3Exception, IOException {
        Resource resource = new Resource(file.getOriginalFilename());
        resource = resourceRepository.save(resource);
        s3Client.createResourceInStorage(file.getBytes(),String.valueOf(resource.getId()));
        return resource;
    }

    public ResponseEntity<ByteArrayResource> getResourceById(Long id) throws ResourceNotFoundException {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource does not exist with given id!"));
        ResponseBytes<GetObjectResponse> s3ResourceBytes = s3Client.getResourceFromStorage(String.valueOf(resource.getId()));
        return ResourceUtility.createResponseForGetResource(s3ResourceBytes,resource);
    }

    public void deleteResources(List<Long> ids) throws ResourceS3Exception {
        resourceRepository.deleteAllById(ids);
        s3Client.deleteResourcesFromStorage(ids);
    }



}
