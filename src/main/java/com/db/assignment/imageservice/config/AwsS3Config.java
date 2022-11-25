package com.db.assignment.imageservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsS3Config {

    @Value("${source-root-url}")
    private String endpointUrl;
    @Value("${aws-s3-endpoint}")
    private String bucket;
    @Value("${aws-accessKey}")
    private String accessKey;
    @Value("${aws-secretKey}")
    private String secretKey;
}
