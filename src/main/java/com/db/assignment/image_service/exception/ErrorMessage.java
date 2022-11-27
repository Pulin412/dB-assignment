package com.db.assignment.image_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorMessage {

    private HttpStatus statusCode;
    private LocalDateTime timestamp;
    private String message;
}
