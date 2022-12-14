package com.epam.epmcacm.resourceservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty
    private long id;

    @JsonProperty
    private String name;

    @JsonProperty
    private Long storageId;

    public Resource() {}

    public Resource(String name) {
        this.name = name;
    }

    public Resource(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public Long getStorageId() {
        return storageId;
    }

    public void setStorageId(Long storageId) {
        this.storageId = storageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String fileName) {
        this.name = fileName;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", storageId='" + storageId + '\'' +
                '}';
    }
}
