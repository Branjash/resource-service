package com.epam.epmcacm.resourceservice.rest;

import com.epam.epmcacm.resourceservice.exceptions.ResourceS3Exception;
import com.epam.epmcacm.resourceservice.model.Resource;
import com.epam.epmcacm.resourceservice.service.api.ResourceService;
import com.epam.epmcacm.resourceservice.util.FileUtil;
import com.epam.epmcacm.resourceservice.util.ResourceUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping(path = "/resources")
public class ResourceController {
    @Autowired
    ResourceService resourceService;

    @PostMapping
    public ResponseEntity<?> saveResource(@RequestParam("file") MultipartFile file) {
        try {
            FileUtil.validateMultipartRequest(file);
            Resource resource = resourceService.saveResource(file);
            resourceService.sendResourceKafkaEvent(resource);
            return ResourceUtility.singlePropertyOkResponse("id", resource.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (ResourceS3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateResource(@PathVariable("id") Long id,@RequestParam("file") MultipartFile file) {
        try {
            FileUtil.validateMultipartRequest(file);
            Resource resource = resourceService.updateResource(id,file);
            return ResourceUtility.singlePropertyOkResponse("id", resource.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (ResourceS3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getResourceById(@PathVariable("id") Long id) {
            return resourceService.getResourceById(id);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteResourcesWithIds(@RequestParam("id") List<Long> ids) {
        try {
            resourceService.deleteResources(ids);
            return ResourceUtility.singlePropertyOkResponse("ids",ids);
        } catch (ResourceS3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());

        }
    }

}
