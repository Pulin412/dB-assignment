package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.model.S3ImageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class S3StoreImpl implements S3StoreInterface{

    @Value("${com.dbf.s3-service}")
    private String mocked_externalS3;

    @Value("${com.dbf.ext-source-service}")
    private String mocked_external_source;

    @Value("${mock.response.save}")
    private String mock_response_save;

    @Value("${mock.response.optimise}")
    private String mock_response_optimise;

    @Value("${mock.response.getOptimisedImageFromS3}")
    private String mock_response_getOptimisedImageFromS3;

    @Value("${mock.response.getOriginalImageFromS3}")
    private String mock_response_getOriginalImageFromS3;

    @Value("${mock.response.flushImage}")
    private Boolean mock_response_flushImage;

    @Value("${mock.response.doesObjectExist}")
    private Boolean mock_response_doesObjectExist;

    public String getOptimisedImageFromS3(S3ImageDto s3ImageDto) {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return mock_response_getOptimisedImageFromS3;
    }

    @Override
    public String getOriginalImageFromS3(S3ImageDto s3ImageDto) {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return mock_response_getOriginalImageFromS3;
    }

    @Override
    public String save(S3ImageDto s3ImageDto) throws CustomS3Exception {
        // Call to the external system to save in S3 [mocked_externalS3]
//        throw new CustomS3Exception("error");
        return mock_response_save;
    }

    @Override
    public boolean flushImage(S3ImageDto s3ImageDto) {
        // Call to the external system to delete from S3 [mocked_externalS3]
        return mock_response_flushImage;
    }

    @Override
    public List<String> getBuckets() {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return Arrays.asList("europe", "us", "ap", "china");
    }

    @Override
    public boolean doesObjectExist(S3ImageDto s3ImageDto) {
        // Call to the external system to check from S3 [mocked_externalS3]
        return mock_response_doesObjectExist;
    }

    @Override
    public String optimise(S3ImageDto s3ImageDto) {
        return mock_response_optimise;
    }
}
