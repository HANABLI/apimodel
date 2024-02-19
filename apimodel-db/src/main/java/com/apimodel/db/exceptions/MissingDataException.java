package com.apimodel.db.exceptions;

import java.io.Serial;

public class MissingDataException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public MissingDataException(String message) {
        super(message);
    }
}
