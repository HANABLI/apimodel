package com.apimodel.rest.security;

import java.util.function.Consumer;

import com.apimodel.model.RapidApiPrincipal;
import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

@Provider
@Priority(2)
public class AccessLogFilter implements ContainerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger("access-log");

    private final Consumer<String> logConsumer;

    public AccessLogFilter() {
        this(LOGGER::info);
    }

    public AccessLogFilter(Consumer<String> logConsumer) {
        this.logConsumer = logConsumer;
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) {
        String user = Stream.of(containerRequestContext.getSecurityContext())
                .filter(context -> context instanceof RapidApiSecurityContext)
                .map(context -> (RapidApiSecurityContext) context)
                .map(RapidApiSecurityContext::getUserPrincipal)
                .filter(principal -> principal instanceof RapidApiPrincipal)
                .map(principal -> (RapidApiPrincipal) principal)
                .map(RapidApiPrincipal::getUser)
                .findFirst()
                .orElse("?");

        String method = containerRequestContext.getMethod();
        String path = containerRequestContext.getUriInfo().getAbsolutePath().getPath();

        logConsumer.accept(String.format("%s => %s %s", user, method, path));
    }

}
