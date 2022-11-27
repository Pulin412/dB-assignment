package com.db.assignment.image_service.config;

import com.db.assignment.image_service.exception.CustomS3Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

@Configuration
public class DefaultListenerSupport extends RetryListenerSupport {

    private final Logger log = LoggerFactory.getLogger(DefaultListenerSupport.class);

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if(context.getLastThrowable().getClass() == CustomS3Exception.class)
            log.warn("IMAGE_SERVICE ::::: Issue in connecting with external systems, retrying..");
        super.onError(context, callback, throwable);
    }

}
