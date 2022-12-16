package com.epam.epmcacm.resourceservice.rest;

import feign.Headers;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "storage-api",value = "storage-api", url = "${rest.client.storage-api.url}", configuration = StorageServiceRestClient.MultipartSupportConfig.class)
public interface StorageServiceRestClient {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<?> createResourceInStorage(@RequestPart("file") MultipartFile file);
    @GetMapping("{id}")
    ResponseEntity<ByteArrayResource> getResourceFromStorage(@PathVariable("id") Long storageId);
    @DeleteMapping
    ResponseEntity<?> deleteResourcesFromStorage(@RequestParam("id") List<Long> ids);


    @Configuration
    public class MultipartSupportConfig {

        @Autowired
        private ObjectFactory<HttpMessageConverters> messageConverters;

        @Bean
        @Primary
        @Scope("prototype")
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder(new SpringEncoder(messageConverters));
        }
    }
}