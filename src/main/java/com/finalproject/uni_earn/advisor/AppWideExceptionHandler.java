package com.finalproject.uni_earn.advisor;

import com.finalproject.uni_earn.exception.DuplicateEmailException;
import com.finalproject.uni_earn.exception.DuplicateUserNameException;
import com.finalproject.uni_earn.exception.InvalidRoleException;
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

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<StandardResponse> handleInvalidRole(InvalidRoleException ex) {
        return new ResponseEntity<StandardResponse>(
                new StandardResponse(400, ex.getMessage(), null),
                HttpStatus.BAD_REQUEST);
    }
}
