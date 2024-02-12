package com.apimodel.rest.security;

import com.apimodel.model.RapidApiPrincipal;
import com.apimodel.model.Subscription;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class AccessLogFilterTest {

    @Test
    public void testFilterNoUser() {
        List<String> logList = new ArrayList<>();
        AccessLogFilter accessLogFilter = new AccessLogFilter(logList::add);

        Principal principal = new RapidApiPrincipal("proxy-secret", "user", Subscription.BASIC);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);


        Mockito.when(securityContext.getUserPrincipal()).thenReturn(principal);
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(URI.create("https://localhost/test"));
        ContainerRequestContext containerRequestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(containerRequestContext.getMethod()).thenReturn("GET");
        Mockito.when(containerRequestContext.getUriInfo()).thenReturn(uriInfo);
        accessLogFilter.filter(containerRequestContext);

        Assertions.assertEquals(1, logList.size());
        Assertions.assertEquals("? => GET /test", logList.get(0));
    }

    @Test
    public void testFilterWithUser() {
        List<String> logList = new ArrayList<>();
        AccessLogFilter accessLogFilter = new AccessLogFilter(logList::add);

        Principal principal = new RapidApiPrincipal("proxy-secret", "user", Subscription.BASIC);
        RapidApiSecurityContext securityContext = Mockito.mock(RapidApiSecurityContext.class);
        Mockito.when(securityContext.getUserPrincipal()).thenReturn(principal);
        UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getAbsolutePath()).thenReturn(URI.create("https://localhost/test"));
        ContainerRequestContext containerRequestContext = Mockito.mock(ContainerRequestContext.class);
        Mockito.when(containerRequestContext.getSecurityContext()).thenReturn(securityContext);
        Mockito.when(containerRequestContext.getMethod()).thenReturn("GET");
        Mockito.when(containerRequestContext.getUriInfo()).thenReturn(uriInfo);
        accessLogFilter.filter(containerRequestContext);

        Assertions.assertEquals(1, logList.size());
        Assertions.assertEquals("user => GET /test", logList.get(0));
    }
}
