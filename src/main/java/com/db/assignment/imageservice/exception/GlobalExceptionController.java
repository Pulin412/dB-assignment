package com.db.assignment.imageservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(ImageNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(ImageNotFoundException ex, WebRequest request) {

        return new ErrorMessage(
                HttpStatus.NOT_FOUND,
                LocalDateTime.now(),
                ex.getMessage());
    }

    @ExceptionHandler(CustomS3Exception.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage customException(CustomS3Exception ex, WebRequest request) {

        return new ErrorMessage(
                HttpStatus.SERVICE_UNAVAILABLE,
                LocalDateTime.now(),
                ex.getMessage());
    }

    @ExceptionHandler(GenericException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage genericException(GenericException ex, WebRequest request) {

        return new ErrorMessage(
                HttpStatus.NOT_ACCEPTABLE,
                LocalDateTime.now(),
                ex.getMessage());
    }
}
