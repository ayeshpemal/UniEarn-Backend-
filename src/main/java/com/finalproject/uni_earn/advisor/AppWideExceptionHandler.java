package com.finalproject.uni_earn.advisor;

import com.finalproject.uni_earn.exception.*;
import com.finalproject.uni_earn.exception.DuplicateEmailException;
import com.finalproject.uni_earn.exception.DuplicateUserNameException;
import com.finalproject.uni_earn.exception.InvalidValueException;
import com.finalproject.uni_earn.exception.NotFoundException;
import com.finalproject.uni_earn.util.StandardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppWideExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<StandardResponse> handleNotFoundException(NotFoundException ex){
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(404, ex.getMessage(), null),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateUserNameException.class)
    public ResponseEntity<StandardResponse> handleDuplicateUserName(DuplicateUserNameException ex) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(409, ex.getMessage(), null),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<StandardResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(409, ex.getMessage(), null),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidValueException.class)
    public ResponseEntity<StandardResponse> handleInvalidValue(InvalidValueException ex) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(400, ex.getMessage(), null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<StandardResponse> handleAlreadyExist(AlreadyExistException ex) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(409, ex.getMessage(), null),
                HttpStatus.CONFLICT);
    }
				
    @ExceptionHandler(InvalidParametersException.class)
    public ResponseEntity<StandardResponse> handleInvalidParameters(InvalidParametersException ex) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(400, ex.getMessage(), null),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotSendException.class)
    public ResponseEntity<StandardResponse> handleEmailNotSend(EmailNotSendException ex) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(500, ex.getMessage(), null),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
