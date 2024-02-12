package com.apimodel.rest.security;

import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

@Provider
public class SecurityFilter implements ContainerResponseFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);
    private Optional<String> getHeader(ContainerRequestContext context, String headerName) {
        return Stream.of(context.getHeaders())
                .filter(Objects::nonNull)
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .filter(entry -> entry.getKey().equalsIgnoreCase(headerName))
                .map(Entry::getValue)
                .flatMap(Collection::stream)
                .findFirst();
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) {
        Optional<String> proxySecret = getHeader(containerRequestContext, SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader());
        Optional<String> user = getHeader(containerRequestContext, SecurityHeader.RAPIDAPI_USER.getHeader());
        Optional<Subscription> subscription = getHeader(containerRequestContext, SecurityHeader.RAPID_SUBSCRIPTION.getHeader()).flatMap(Subscription::from);

        if (proxySecret.isEmpty()) {
            throw new NotAuthorizedException("Missing security header: " +
                    SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader());
        }

        if (user.isEmpty()) {
            throw new NotAuthorizedException("Missing security header: " +
                    SecurityHeader.RAPIDAPI_USER.getHeader());
        }

        if (subscription.isEmpty()) {
            throw new NotAuthorizedException("Missing or invalid security header: " +
                    SecurityHeader.RAPID_SUBSCRIPTION.getHeader());
        }

        RapidApiPrincipal principal = new RapidApiPrincipal(proxySecret.get(), user.get(), subscription.get());
        LOGGER.info("User Principal: {}", principal);
        containerRequestContext.setSecurityContext(new RapidApiSecurityContext(principal));
    }
}
