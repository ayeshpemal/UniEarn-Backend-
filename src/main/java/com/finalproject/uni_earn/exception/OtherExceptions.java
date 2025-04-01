package com.finalproject.uni_earn.exception;

// This class is a placeholder for other exceptions that may be defined in the future.

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class OtherExceptions extends RuntimeException {
    public OtherExceptions(String message) {
        super(message);
    }
}
