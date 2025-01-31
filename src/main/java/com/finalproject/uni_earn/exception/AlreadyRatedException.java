package com.finalproject.uni_earn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class AlreadyRatedException extends RuntimeException {
    public AlreadyRatedException(String message) {
        super(message);
    }
}
