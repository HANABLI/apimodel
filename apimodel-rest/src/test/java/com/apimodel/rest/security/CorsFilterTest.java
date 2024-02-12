package com.apimodel.rest.security;

import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

public class CorsFilterTest {
    @Test
    public void testFilter() {
        MultivaluedMap<String, Object> headerMap = new MultivaluedHashMap<>();
        ContainerResponseContext containerRequestContext = Mockito.mock(ContainerResponseContext.class);
        Mockito.when(containerRequestContext.getHeaders()).thenReturn(headerMap);

        new CorsFilter().filter(null, containerRequestContext);

        Assertions.assertEquals(3, headerMap.size());
        Assertions.assertTrue(headerMap.containsKey("Access-Control-Allow-Origin"));
        Assertions.assertTrue(headerMap.containsKey("Access-Control-Allow-Methods"));
        Assertions.assertEquals("[*]", headerMap.get("Access-Control-Allow-Origin").toString());
        Assertions.assertEquals("[GET, PUT, POST, DELETE, HEAD, OPTIONS, PATCH]",
                headerMap.get("Access-Control-Allow-Methods").toString());
    }
}
