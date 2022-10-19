package com.epam.epmcacm.resourceservice.service.api;

import com.epam.epmcacm.resourceservice.exceptions.ResourceS3Exception;
import com.epam.epmcacm.resourceservice.model.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ResourceService {

    Resource saveResource(MultipartFile file) throws ResourceS3Exception, IOException;
    Resource sendResourceKafkaEvent(Resource resource);
    Resource updateResource(Long id, MultipartFile file) throws ResourceNotFoundException, IOException, ResourceS3Exception;
    ResponseEntity<ByteArrayResource> getResourceById(Long id) throws ResourceNotFoundException;
    void deleteResources(List<Long> ids) throws ResourceS3Exception;
}
