package com.epam.epmcacm.resourceservice.rest;

import com.epam.epmcacm.resourceservice.model.Resource;
import com.epam.epmcacm.resourceservice.util.ResourceUtility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(ResourceController.class)
public class ResourceControllerTest {

    @MockBean
    ResourceService resourceService;

    @Autowired
    MockMvc mockMvc;

    Resource RESOURCE = new Resource(1L, "First.mp3");

    @Test
    public void getResourceByIdTest_success() throws Exception {
        MockMultipartFile sampleFile = createMockMultipartFile();
        ResponseEntity<ByteArrayResource> mockResponse = createMockResponseEntity(sampleFile);

        Mockito.when(resourceService.getResourceById(RESOURCE.getId())).thenReturn(mockResponse);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/resources/1")
                        .contentType(sampleFile.getContentType()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", notNullValue()))
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void saveResourceTest_success() throws Exception {
        MockMultipartFile sampleFile = createMockMultipartFile();

        Mockito.when(resourceService.saveResourceAndSendEvent(sampleFile)).thenReturn(RESOURCE);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/resources")
                        .file(sampleFile))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", notNullValue()))
                        .andExpect(jsonPath("$.id", is(1)))
                        .andDo(MockMvcResultHandlers.print());

    }

    @Test
    public void updateResourceTest_success() throws Exception {
        MockMultipartFile sampleFile = createMockMultipartFile();

        Mockito.when(resourceService.updateResource(RESOURCE.getId(), sampleFile)).thenReturn(RESOURCE);

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart(HttpMethod.PUT,"/resources/1")
                        .file(sampleFile))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", notNullValue()))
                        .andExpect(jsonPath("$.id", is(1)))
                        .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteResourceTest_success() throws Exception {
        List<Long> ids = Arrays.asList(new Long[]{1l});
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/resources?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", notNullValue()))
                        .andExpect(content().json("{'ids':[1]}"))
                        .andDo(MockMvcResultHandlers.print());

    }

    private MockMultipartFile createMockMultipartFile() {
        String fileName = "sampleFile.txt";
        MockMultipartFile sampleFile = new MockMultipartFile(
                "file",
                fileName,
                "text/plain",
                "This is the file content".getBytes()
        );
        return sampleFile;
    }

    private ResponseEntity<ByteArrayResource> createMockResponseEntity(MockMultipartFile file) throws IOException {
        ByteArrayResource byteResource = new ByteArrayResource(file.getBytes());
        return ResponseEntity.ok()
                .headers(ResourceUtility.createResponseHeadersForResourceDownload(RESOURCE.getName()))
                .contentLength(file.getSize())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteResource);
    }

}
