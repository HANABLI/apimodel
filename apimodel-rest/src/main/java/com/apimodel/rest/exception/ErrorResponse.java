package com.apimodel.rest.exception;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public class ErrorResponse {
    private final int status;
    private final String message;

    @JsonCreator
    public ErrorResponse(@JsonProperty("status") int status,
                         @JsonProperty("message") String message) {
        this.status = status;
        this.message = message;
    }

    @Schema(
            name = "status",
            title = "Status",
            description = """
                    Specifies the HTTP status code returned after the error condition.
                    """,
            example = """
                    400
                    404
                    500
                    """
    )

    public int getStatus() {
        return status;
    }

    @Schema(
            name = "message",
            title = "Message",
            description = """
                    Provide a brief message describing the error that occurred.
                    """,
            example = """
                    A list with id list-id was not found
                    """
    )

    public String getMessage() {
        return message;
    }
}
