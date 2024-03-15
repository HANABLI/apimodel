package com.apimodel.rest.security;

import com.apimodel.db.ServiceFactory;
import com.apimodel.db.service.ApiKeyService;
import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import com.apimodel.model.config.ConfigKey;
import com.apimodel.model.user.ApiKey;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.net.URI;
import java.util.Properties;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


public class SecurityFilterTest {
    private String testNotAuthorized(MultivaluedMap<String, String> headers) {

        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getRequestUri()).thenReturn(URI.create("https://apimodel.com/api/lists"));

        ContainerRequestContext containerRequestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(containerRequestContext.getHeaders()).thenReturn(headers);
        Mockito.when(containerRequestContext.getUriInfo()).thenReturn(uriInfo);

        Properties configProperties = new Properties();
        configProperties.setProperty(ConfigKey.RAPIDAPI_PROXY_SECRET.getKey(), "secret");
        Config config = ConfigFactory.parseProperties(configProperties);

        ServiceFactory serviceFactory = Mockito.mock(ServiceFactory.class);

        NotAuthorizedException notAuthorizedException = Assertions.assertThrows(NotAuthorizedException.class,
                () -> new SecurityFilter(config, serviceFactory).filter(containerRequestContext));
        return notAuthorizedException.getMessage();
    }

    @Test
    public void testFilterNoHeaders() {
        String errorMessage = testNotAuthorized(new MultivaluedHashMap<>());
        Assertions.assertEquals("Missing security header: X-RapidAPI-Proxy-Secret",  errorMessage);
    }

    @Test
    public void testFilterWithProxySecretHeader() {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.putSingle(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret");
        String errorMessage = testNotAuthorized(headers);
        Assertions.assertEquals("Missing security header: X-RapidAPI-User", errorMessage);
    }

    @Test
    public void testFilterWithProxySecretAndUserHeaders() {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.putSingle(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret");
        headers.putSingle(SecurityHeader.RAPIDAPI_USER.getHeader(), "proxy-user");
        String errorMessage = testNotAuthorized(headers);
        Assertions.assertEquals("Missing or invalid security header: X-RapidAPI-Subscription", errorMessage);
    }

    @Test
    public void testFilterWithInvalidSubscription() {
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.putSingle(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret");
        headers.putSingle(SecurityHeader.RAPIDAPI_USER.getHeader(), "proxy-user");
        headers.putSingle(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), "invalid");
        String errorMessage = testNotAuthorized(headers);
        Assertions.assertEquals("Missing or invalid security header: X-RapidAPI-Subscription", errorMessage);
    }

    @Test
    public void testFilterInvalidProxySecret() {
        RapidApiPrincipal principal = new RapidApiPrincipal("proxy-ivalid", "user", Subscription.BASIC);

        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getRequestUri()).thenReturn(URI.create("https://apimodel.com/api/lists"));


        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.putSingle(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), principal.getProxySecret());
        headers.putSingle(SecurityHeader.RAPIDAPI_USER.getHeader(), principal.getUser());
        headers.putSingle(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), principal.getSubscription().name());

        ContainerRequestContext containerRequestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(containerRequestContext.getHeaders()).thenReturn(headers);
        Mockito.when(containerRequestContext.getUriInfo()).thenReturn(uriInfo);

        Properties configProperties = new Properties();
        configProperties.setProperty(ConfigKey.RAPIDAPI_PROXY_SECRET.getKey(), "proxy-secret");
        Config config = ConfigFactory.parseProperties(configProperties);

        ServiceFactory serviceFactory = Mockito.mock(ServiceFactory.class);

        NotAuthorizedException notAuthorized = Assertions.assertThrows(NotAuthorizedException.class,
                () -> new SecurityFilter(config, serviceFactory).filter(containerRequestContext));

        Assertions.assertEquals("Invalid proxy secret.", notAuthorized.getMessage());

        Mockito.verify(containerRequestContext, Mockito.times(0)).setSecurityContext(any());
    }

    @Test
    public void testFilterSuccessful() {
        RapidApiPrincipal principal = new RapidApiPrincipal("proxy-secret", "proxy-user", Subscription.BASIC);
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getRequestUri()).thenReturn(URI.create("https://apimodel.com/api/lists"));

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.putSingle(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), principal.getProxySecret());
        headers.putSingle(SecurityHeader.RAPIDAPI_USER.getHeader(), principal.getUser());
        headers.putSingle(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), principal.getSubscription().name());

        ContainerRequestContext containerRequestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(containerRequestContext.getHeaders()).thenReturn(headers);
        Mockito.when(containerRequestContext.getUriInfo()).thenReturn(uriInfo);

        Properties configProperties = new Properties();
        configProperties.setProperty(ConfigKey.RAPIDAPI_PROXY_SECRET.getKey(), "proxy-secret");
        Config config = ConfigFactory.parseProperties(configProperties);
        ServiceFactory serviceFactory = Mockito.mock(ServiceFactory.class);

        new SecurityFilter(config, serviceFactory).filter(containerRequestContext);

        RapidApiSecurityContext securityContext = new RapidApiSecurityContext(principal);
        Mockito.verify(containerRequestContext, Mockito.times(1)).setSecurityContext(ArgumentMatchers.refEq(securityContext));
    }

    @Test
    public void testApiKeySuccessful() {
        RapidApiPrincipal principal = new RapidApiPrincipal("key", "user", Subscription.BASIC);

        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getRequestUri()).thenReturn(URI.create("https://apimodel.com/api/lists"));

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.putSingle(SecurityHeader.EXEMPLE_API_KEY.getHeader(), "key");

        ContainerRequestContext containerRequestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(containerRequestContext.getHeaders()).thenReturn(headers);
        Mockito.when(containerRequestContext.getUriInfo()).thenReturn(uriInfo);

        ApiKeyService apiKeyService = Mockito.mock(ApiKeyService.class);
        ApiKey apiKey = new ApiKey().setApiKey("key").setUser("user").setSubscription(Subscription.BASIC);
        Mockito.when(apiKeyService.get(eq("key"))).thenReturn(Optional.of(apiKey));

        ServiceFactory serviceFactory = Mockito.mock(ServiceFactory.class);
        Mockito.when(serviceFactory.getApiKeyService()).thenReturn(apiKeyService);

        new SecurityFilter(ConfigFactory.empty(), serviceFactory).filter(containerRequestContext);

        RapidApiSecurityContext securityContext = new RapidApiSecurityContext(principal);
        Mockito.verify(containerRequestContext, Mockito.times(1)).setSecurityContext(ArgumentMatchers.refEq(securityContext));

    }

    @Test
    public void testApiKeyNotFound() {
        RapidApiPrincipal principal = new RapidApiPrincipal("key", "user", Subscription.BASIC);

        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getRequestUri()).thenReturn(URI.create("https://apimodel.com/api/lists"));

        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.putSingle(SecurityHeader.EXEMPLE_API_KEY.getHeader(), "key");

        ContainerRequestContext containerRequestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(containerRequestContext.getHeaders()).thenReturn(headers);
        Mockito.when(containerRequestContext.getUriInfo()).thenReturn(uriInfo);

        ApiKeyService apiKeyService = Mockito.mock(ApiKeyService.class);
        ApiKey apiKey = new ApiKey().setApiKey("key").setUser("user").setSubscription(Subscription.BASIC);
        Mockito.when(apiKeyService.get(eq("key"))).thenReturn(Optional.empty());

        ServiceFactory serviceFactory = Mockito.mock(ServiceFactory.class);
        Mockito.when(serviceFactory.getApiKeyService()).thenReturn(apiKeyService);

        NotAuthorizedException notAuthorized = Assertions.assertThrows(NotAuthorizedException.class,
                () -> new SecurityFilter(ConfigFactory.empty(), serviceFactory).filter(containerRequestContext));
        Assertions.assertEquals("Invalid API Key", notAuthorized.getMessage());

        Mockito.verify(containerRequestContext, Mockito.times(0)).setSecurityContext(any());
    }
}
