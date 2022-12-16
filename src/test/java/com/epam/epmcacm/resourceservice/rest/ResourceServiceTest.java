package com.epam.epmcacm.resourceservice.rest;

import com.epam.epmcacm.resourceservice.model.Resource;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceServiceTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ResourceRepository resourceRepository;

    List<Path> filesToBeDeleted = new ArrayList<>();

    Resource RESOURCE_1 = new Resource(1L, "sampleFile.txt");

    @Test
    public void getResourceByIdTest_success()  {
        Mockito.when(resourceRepository.findById(RESOURCE_1.getId())).thenReturn(Optional.of(RESOURCE_1));
    }

}
