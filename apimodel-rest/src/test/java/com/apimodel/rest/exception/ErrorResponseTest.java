package com.apimodel.rest.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ErrorResponseTest {

    @Test
    public void testConstructor() {
        ErrorResponse errorResponse = new ErrorResponse(401, "Unauthorized");
        Assertions.assertEquals(401, errorResponse.getStatus());
        Assertions.assertEquals("Unauthorized", errorResponse.getMessage());
    }

    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        ErrorResponse errorResponse = new ErrorResponse(401, "Unauthorized");
        String jsonObj = new ObjectMapper().writeValueAsString(errorResponse);
        String expectedObj = "{\"status\":401,\"message\":\"Unauthorized\"}";
        Assertions.assertEquals(expectedObj, jsonObj);
    }

    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        String jsonObj = "{\"status\":401,\"message\":\"Unauthorized\"}";
        ErrorResponse errorResponse = new ObjectMapper().readValue(jsonObj, ErrorResponse.class);
        ErrorResponse expectedObj = new ErrorResponse(401, "Unauthorized");
        Assertions.assertEquals(expectedObj.getStatus(), errorResponse.getStatus());
        //Assertions.assertEquals(expectedObj.toString(), errorResponse.toString());
    }
}

