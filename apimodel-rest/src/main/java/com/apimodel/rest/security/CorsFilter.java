package com.apimodel.rest.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(2)
public class CorsFilter implements ContainerResponseFilter  {
    @Override
    public void filter(ContainerRequestContext containerRequestContext,
                       ContainerResponseContext containerResponseContext) {

        containerResponseContext.getHeaders().add("My-Custom-Header", "Value");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        containerResponseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, PUT, POST, DELETE, HEAD, OPTIONS, PATCH"
        );
    }
}
