package com.apimodel.rest.resource;


import com.apimodel.db.ServiceFactory;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/test")
public class HelloResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloResource.class);
    private final ServiceFactory serviceFactory;

    @Inject
    public HelloResource(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
        LOGGER.info("Initializing service factory to: {}", serviceFactory);
    }

    @GET
    @Produces(TEXT_PLAIN)
    public String test() {
        return "Hello";
    }
}
