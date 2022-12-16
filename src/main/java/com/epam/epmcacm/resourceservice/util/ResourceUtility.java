package com.epam.epmcacm.resourceservice.util;

import com.epam.epmcacm.resourceservice.exceptions.ResourceS3Exception;
import com.epam.epmcacm.resourceservice.model.Resource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ResourceUtility {

    public static final String MOCK_FILE_NAME = "Sample.mp3";

    private ResourceUtility() {
        throw new IllegalStateException("Utility class!");
    }


    public static HttpHeaders createResponseHeadersForResourceDownload(String fileName) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        responseHeaders.add("Cache-Control", "no-cache, no-store, must-revalidate");
        responseHeaders.add("Pragma", "no-cache");
        responseHeaders.add("Expires", "0");
        return responseHeaders;
    }

    public static ResponseEntity<?> singlePropertyOkResponse(@NonNull String key, @NonNull Object value) {
        Map<String,Object> result = new HashMap<>();
        result.put(key, value);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    public static Long saveInStorageAndGetId(MultipartFile file, Function<MultipartFile, ResponseEntity<?>> storageServiceCall) throws ResourceS3Exception {
        ResponseEntity<?> result = storageServiceCall.apply(file);
        if(result.getStatusCode() != HttpStatus.OK) throw new ResourceS3Exception("Error trying to create S3 resource successfully!");
        if(!result.hasBody()) throw new ResourceS3Exception("There is no id after creating file in storage service!");
        return ((Integer) ((Map<String,Object>) result.getBody()).get("id")).longValue();
    }

}
