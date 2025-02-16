package com.finalproject.uni_earn.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmailNotSendException extends RuntimeException{
    public EmailNotSendException(String message) {
        super(message);
    }
}
