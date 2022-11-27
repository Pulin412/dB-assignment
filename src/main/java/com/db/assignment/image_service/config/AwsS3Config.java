package com.db.assignment.image_service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AwsS3Config {

    @Value("${source-root-url}")
    private String endpointUrl;
    @Value("${aws-s3-endpoint}")
    private String bucket;
    @Value("${aws-accessKey}")
    private String accessKey;
    @Value("${aws-secretKey}")
    private String secretKey;

//    public AWSCredentials credentials() {
//        AWSCredentials credentials = new BasicAWSCredentials(
//                "accesskey",
//                "secretKey"
//        );
//        return credentials;
//    }
//
//    @Bean
//    public AmazonS3 amazonS3() {
//        AmazonS3 s3client = AmazonS3ClientBuilder
//                .standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials()))
//                .withRegion(Regions.US_EAST_1)
//                .build();
//        return s3client;
//    }
}
