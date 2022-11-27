package com.db.assignment.image_service.exception;

public class CustomS3Exception extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public CustomS3Exception(String msg) {
        super(msg);
    }
}
