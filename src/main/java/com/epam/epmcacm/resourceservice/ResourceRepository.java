package com.epam.epmcacm.resourceservice;

import com.epam.epmcacm.resourceservice.model.Resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceRepository extends CrudRepository<Resource, Long> {
}
