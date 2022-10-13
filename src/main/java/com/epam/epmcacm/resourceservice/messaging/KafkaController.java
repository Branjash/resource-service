package com.epam.epmcacm.resourceservice.messaging;

import com.epam.epmcacm.resourceservice.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/testKafka")
@RestController
public class KafkaController {

    final KafkaTemplate kafkaTemplate;

    Logger logger = LoggerFactory.getLogger(KafkaController.class);

    public KafkaController(KafkaTemplate kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping
    public String sentMessage(@RequestBody Resource resource) {
        this.kafkaTemplate.send(KafkaProducer.TOPIC_RESOURCES, String.valueOf(resource.getId()),resource);
        return "SUCCESS!";
    }

}
