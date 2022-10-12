package com.epam.epmcacm.resourceservice;

import com.epam.epmcacm.resourceservice.model.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class ResourceUtility {

    private ResourceUtility() {
        throw new IllegalStateException("Utility class!");
    }

    public static ResponseEntity<ByteArrayResource> createResponseForGetResource(ResponseBytes<GetObjectResponse> responseBytes, Resource resource){
        GetObjectResponse responseObject = responseBytes.response();
        ByteArrayResource byteResource = new ByteArrayResource(responseBytes.asByteArray());
        return ResponseEntity.ok()
                .headers(createResponseHeadersForResourceDownload(resource.getName()))
                .contentLength(responseObject.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteResource);
    }

    public static HttpHeaders createResponseHeadersForResourceDownload(String fileName) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        responseHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
        responseHeaders.add("Pragma", "no-cache");
        responseHeaders.add("Expires", "0");
        return responseHeaders;
    }

}
