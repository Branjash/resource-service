package com.epam.epmcacm.resourceservice.exceptions;

public class ResourceNotFoundException extends  Exception{

    public ResourceNotFoundException(String message)
    {
        super(message);
    }
}
