package com.db.assignment.imageservice.exception;

public class CustomS3Exception extends RuntimeException{

    private static final long serialVersionUID = 1L;

    public CustomS3Exception(String msg) {
        super(msg);
    }
}
