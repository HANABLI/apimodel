package com.apimodel.rest.security;

import com.apimodel.db.ServiceFactory;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import com.apimodel.model.config.ConfigKey;
import com.apimodel.model.user.ApiKey;
import com.typesafe.config.Config;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Stream;

@Provider
@Priority(1)
public class SecurityFilter implements ContainerRequestFilter {
    //private static final Logger LOGGER = LoggerFactory.getLogger(SecurityFilter.class);
    private final Config config;
    private final ServiceFactory serviceFactory;
    @Inject
    public SecurityFilter(Config config, ServiceFactory serviceFactory) {
        this.config = config;
        this.serviceFactory = serviceFactory;
    }

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
    public void filter(ContainerRequestContext containerRequestContext) {
        if (containerRequestContext.getUriInfo().getRequestUri().getPath().contains("/api/openapi")) {
            return;
        }
        RapidApiPrincipal principal = null;
        Optional<String> apikey = getHeader(containerRequestContext, SecurityHeader.EXEMPLE_API_KEY.getHeader());
        if (apikey.isPresent()) {
            ApiKey key = serviceFactory.getApiKeyService().get(apikey.get())
                    .orElseThrow(() -> new NotAuthorizedException("Invalid API Key", Response.status(Response.Status.UNAUTHORIZED)));
            principal = new RapidApiPrincipal(key.getApiKey(), key.getUser(), key.getSubscription());

        } else {

            Optional<String> proxySecret = getHeader(containerRequestContext, SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader());
            Optional<String> user = getHeader(containerRequestContext, SecurityHeader.RAPIDAPI_USER.getHeader());
            Optional<Subscription> subscription = getHeader(containerRequestContext, SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader()).flatMap(Subscription::from);

            if (proxySecret.isEmpty()) {
                throw new NotAuthorizedException("Missing security header: " +
                        SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), Response.status(Response.Status.UNAUTHORIZED));
            }

            if (user.isEmpty()) {
                throw new NotAuthorizedException("Missing security header: " +
                        SecurityHeader.RAPIDAPI_USER.getHeader(), Response.status(Response.Status.UNAUTHORIZED));
            }

            if (subscription.isEmpty()) {
                throw new NotAuthorizedException("Missing or invalid security header: " +
                        SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), Response.status(Response.Status.UNAUTHORIZED));
            }

            String expectedProxySecret = config.getString(ConfigKey.RAPIDAPI_PROXY_SECRET.getKey());
            if (!proxySecret.get().equals(expectedProxySecret)) {
                throw new NotAuthorizedException("Invalid proxy secret.", Response.status(Response.Status.UNAUTHORIZED));
            }
            principal = new RapidApiPrincipal(proxySecret.get(), user.get(), subscription.get());

        }

        //LOGGER.info("User Principal: {}", principal);
        containerRequestContext.setSecurityContext(new RapidApiSecurityContext(principal));
    }
}
