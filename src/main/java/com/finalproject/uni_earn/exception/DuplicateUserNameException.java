package com.finalproject.uni_earn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateUserNameException extends RuntimeException{
    public DuplicateUserNameException(String message) {
        super(message);
    }
}
