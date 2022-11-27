package com.db.assignment.imageservice.config;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomDbAppender extends AppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        // add code here to save to database
    }
}
