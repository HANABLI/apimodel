package com.apimodel.rest.resource.v1;

import com.apimodel.rest.exception.ErrorResponse;
import jakarta.ws.rs.core.Response;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class BaseResourceIT extends JerseyTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseResourceIT.class);
    protected void verifyErrorResponse(Response response, Response.Status expectedStatus, String expectedErrorMessage) {
        Assertions.assertEquals(expectedStatus.getStatusCode(), response.getStatus());
        Assertions.assertEquals(APPLICATION_JSON_TYPE, response.getMediaType());

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        Assertions.assertEquals(expectedStatus.getStatusCode(), errorResponse.getStatus());
        Assertions.assertEquals(expectedErrorMessage, errorResponse.getMessage());
        LOGGER.info("{}", response.getHeaders());
        verifyCorsHeaders(response);
    }

    protected void verifyCorsHeaders(Response response) {
        Assertions.assertEquals("*", response.getHeaderString("Access-Control-Allow-Origin"));
        Assertions.assertEquals("GET, PUT, POST, DELETE, HEAD, OPTIONS, PATCH",
                response.getHeaderString("Access-Control-Allow-Methods"));
    }
}
