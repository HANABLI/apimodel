package com.apimodel.rest.resource;

import com.apimodel.rest.ApiApplication;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.LogManager;

public class TestResourceTest extends JerseyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestResourceTest.class);
    static {
        LogManager.getLogManager().reset();
    }

    @Override
    protected Application configure() {
        return new ApiApplication();
    }

    @Test
    public void test() {
        Response response = target("/test/hello").request().get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("Hello", response.readEntity(String.class));
        Assertions.assertEquals("*", response.getHeaderString("Access-Control-Allow-Origin"));
        Assertions.assertEquals("GET, PUT, POST, DELETE, HEAD, OPTIONS, PATCH", response.getHeaderString("Access-Control-Allow-Methods"));
        LOGGER.info("{}", response.getHeaders());
    }

}
