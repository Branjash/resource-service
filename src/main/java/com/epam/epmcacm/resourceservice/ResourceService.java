package com.epam.epmcacm.resourceservice;

import com.epam.epmcacm.resourceservice.exceptions.ResourceNotFoundException;
import com.epam.epmcacm.resourceservice.exceptions.ResourceS3Exception;
import com.epam.epmcacm.resourceservice.model.Resource;
import com.epam.epmcacm.resourceservice.s3.S3ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private S3ClientService s3Client;

    public Resource saveResource(MultipartFile file) throws ResourceS3Exception, IOException {
        Resource resource = new Resource(file.getName());
        resource = resourceRepository.save(resource);
        s3Client.putObject(file.getBytes(),String.valueOf(resource.getId()));
        return resource;
    }

    public byte[] getResourceById(Long id) throws ResourceNotFoundException {
        return s3Client.getObjectBytes(String.valueOf(id));
    }

    public void deleteResources(List<Long> ids) {
        resourceRepository.deleteAllById(ids);
        ids.forEach(this::deleteFromStorage);
    }

    private void deleteFromStorage(Long id) {
        try {
            s3Client.deleteObject(String.valueOf(id));
        } catch (ResourceS3Exception e) {
            logger.error(String.format("Error deleting file from storage, id: %s",e.getMessage()));
        }
    }

}
