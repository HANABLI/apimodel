package com.apimodel.rest.resource;

import com.apimodel.model.Subscription;
import com.apimodel.model.config.ConfigKey;
import com.apimodel.rest.ApiApplication;
import com.apimodel.rest.exception.ErrorResponse;
import com.apimodel.rest.security.SecurityHeader;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.JDBC;

import java.util.logging.LogManager;
import java.util.Properties;



public class GetAllTodoListsResourceIT extends JerseyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllTodoListsResourceIT.class);
    static {
        LogManager.getLogManager().reset();
    }

    @Override
    protected Application configure() {
        Properties properties = new Properties();
        properties.setProperty(ConfigKey.DB_DRIVER.getKey(), JDBC.class.getName());
        properties.setProperty(ConfigKey.DB_URL.getKey(), "jdbc:sqlite::memory:");
        properties.setProperty(ConfigKey.DB_USERNAME.getKey(), "user");
        properties.setProperty(ConfigKey.DB_PASSWORD.getKey(), "pass");
        Config config = ConfigFactory.parseProperties(properties);
        return new ApiApplication(config);
    }

    @Test
    public void testNoSecurityHeaders() {
        Response response = target("/v1/lists").request().get();
        Assertions.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        Assertions.assertEquals(401, errorResponse.getStatus());
        Assertions.assertEquals("Missing security header: X-RapidAPI-Proxy-Secret", errorResponse.getMessage());
        LOGGER.info("{}", response.getHeaders());
    }

    @Test
    public void testProxySecretHeaders() {
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .get();
        Assertions.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        Assertions.assertEquals(401, errorResponse.getStatus());
        Assertions.assertEquals("Missing security header: X-RapidAPI-User", errorResponse.getMessage());
        LOGGER.info("{}", response.getHeaders());
    }

    @Test
    public void testProxySecretAndUserHeaders() {
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .get();
        Assertions.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        Assertions.assertEquals(401, errorResponse.getStatus());
        Assertions.assertEquals("Missing or invalid security header: X-RapidAPI-Subscription", errorResponse.getMessage());
        LOGGER.info("{}", response.getHeaders());
    }

    @Test
    public void testInvalidSubscriptionHeaders() {
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_SUBSCRIPTION.getHeader(), "invalid")
                .get();
        Assertions.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        Assertions.assertEquals(401, errorResponse.getStatus());
        Assertions.assertEquals("Missing or invalid security header: X-RapidAPI-Subscription", errorResponse.getMessage());
        LOGGER.info("{}", response.getHeaders());
    }

    @Test
    public void testValidHeaders() {
        Response response = target("/v1/lists").request()
                .header(SecurityHeader.RAPIDAPI_PROXY_SECRET.getHeader(), "proxy-secret")
                .header(SecurityHeader.RAPIDAPI_USER.getHeader(), "user")
                .header(SecurityHeader.RAPID_SUBSCRIPTION.getHeader(), Subscription.BASIC)
                .get();
        Assertions.assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType());
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals("[]" , response.readEntity(String.class));
        Assertions.assertEquals("*", response.getHeaderString("Access-Control-Allow-Origin"));
        Assertions.assertEquals("GET, PUT, POST, DELETE, HEAD, OPTIONS, PATCH",
                response.getHeaderString("Access-Control-Allow-Methods"));
        LOGGER.info("{}", response.getHeaders());
    }

}
