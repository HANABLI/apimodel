package com.apimodel.db.exceptions;

import java.io.Serial;

public class ConstraintException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public ConstraintException(String message) {
        super(message);
    }
}
