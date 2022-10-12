package com.epam.epmcacm.resourceservice;

import com.epam.epmcacm.resourceservice.s3.S3ClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.Bucket;

@Component
@Order(0)
class MyApplicationListener implements ApplicationListener<ApplicationReadyEvent> {


  @Autowired
  S3ClientService s3ClientService;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    s3ClientService.getResourceBucketAndCreateIfNotExists();
  }

}