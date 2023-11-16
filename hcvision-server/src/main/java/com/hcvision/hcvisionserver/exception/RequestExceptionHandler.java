package com.hcvision.hcvisionserver.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RequestExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException ex) {
        String responseBody = "{\"error_msg\": \"" + ex.getMessage() + "\"}";
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFound(NotFoundException ex) {
        String responseBody = "{\"error_msg\": \"" + ex.getMessage() + "\"}";
        return new ResponseEntity<>(responseBody, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<String> handleInternalServerError(InternalServerErrorException ex) {
        String responseBody = "{\"error_msg\": \"" + ex.getMessage() + "\"}";
        return new ResponseEntity<>(responseBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<String> handleInternalServerError(ForbiddenException ex) {
        String responseBody = "{\"error_msg\": \"" + ex.getMessage() + "\"}";
        return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
    }
}
