package com.apimodel.rest.security;

import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SecurityFilterTest {
    private String testNotAuthorized(MultivaluedMap<String, String> headers) {
        ContainerRequestContext containerRequestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(containerRequestContext.getHeaders()).thenReturn(headers);

        NotAuthorizedException notAuthorizedException = Assertions.assertThrows(NotAuthorizedException.class,
                () -> new SecurityFilter().filter(containerRequestContext));
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
    public void testFilterWithAllHeaders() {
        RapidApiPrincipal principal = new RapidApiPrincipal("proxy-secret", "proxy-user", Subscription.BASIC);
        MultivaluedMap<String, String> headers = new MultivaluedHashMap<>();
        headers.putSingle(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), principal.getProxySecret());
        headers.putSingle(SecurityHeader.RAPIDAPI_USER.getHeader(), principal.getUser());
        headers.putSingle(SecurityHeader.RAPIDAPI_SUBSCRIPTION.getHeader(), principal.getSubscription().name());

        ContainerRequestContext containerRequestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(containerRequestContext.getHeaders()).thenReturn(headers);


        new SecurityFilter().filter(containerRequestContext);


        RapidApiSecurityContext securityContext = new RapidApiSecurityContext(principal);
        Mockito.verify(containerRequestContext, Mockito.times(1)).setSecurityContext(ArgumentMatchers.refEq(securityContext));
    }
}
