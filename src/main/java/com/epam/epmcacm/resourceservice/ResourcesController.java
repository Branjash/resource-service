package com.epam.epmcacm.resourceservice;

import com.epam.epmcacm.resourceservice.exceptions.ResourceNotFoundException;
import com.epam.epmcacm.resourceservice.exceptions.ResourceS3Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/resources")
public class ResourcesController {

    @Autowired
    ResourceService resourceService;

    @PostMapping
    public ResponseEntity<?> saveResource(@RequestParam("file") MultipartFile file) {
        try {
            return new ResponseEntity<>(resourceService.saveResource(file),HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected I/0 error");
        } catch (ResourceS3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected s3 storage save error!");
        }
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getResourceById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<>(resourceService.getResourceById(id), HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource doesn’t exist with given id!");
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteResourcesWithIds(@RequestParam("id") List<Long> ids) {
        resourceService.deleteResources(ids);
        Map<String, List<Long>> response = new HashMap<>();
        response.put("ids", ids);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
