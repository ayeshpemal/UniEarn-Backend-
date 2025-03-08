package com.finalproject.uni_earn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UserNotVerifiedException extends RuntimeException {
    public  UserNotVerifiedException(String message) {
        super(message);
    }
}
