package com.epam.epmcacm.resourceservice.rest;

import com.epam.epmcacm.resourceservice.exceptions.ResourceS3Exception;
import com.epam.epmcacm.resourceservice.model.Resource;
import com.epam.epmcacm.resourceservice.util.FileUtil;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
            return singlePropertyOkResponse("id", resource.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected I/0 error");
        } catch (ResourceS3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected s3 storage save error!");
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateResource(@PathVariable("id") Long id,@RequestParam("file") MultipartFile file) {
        try {
            FileUtil.validateMultipartRequest(file);
            Resource resource = resourceService.updateResource(id,file);
            return singlePropertyOkResponse("id", resource.getId());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected I/0 error");
        } catch (ResourceS3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected s3 storage save error!");
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
            return singlePropertyOkResponse("ids",ids);
        } catch (ResourceS3Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected s3 storage delete error!");

        }
    }

    private ResponseEntity<?> singlePropertyOkResponse(@NonNull String key,@NonNull Object value) {
        Map<String,Object> result = new HashMap<>();
        result.put(key, value);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

}
