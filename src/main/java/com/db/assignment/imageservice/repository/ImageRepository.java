package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.model.ImageRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class ImageRepository {

    @Value("${com.dbf.s3-service}")
    private String mocked_externalS3;

    @Value("${com.dbf.ext-source-service}")
    private String mocked_external_source;

    @Value("${mock.response.getOriginalImageFromSource}")
    private String mock_response_getOriginalImageFromSource;

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

    public String getOptimisedImageFromS3(String s3Url) {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return mock_response_getOptimisedImageFromS3;
    }

    public String getOriginalImageFromS3(String s3Url) {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return mock_response_getOriginalImageFromS3;
    }

    public String getOriginalImageFromSource(ImageRequestDto imageRequestDto) {
        // Call to the external system to fetch from source [mocked_external_source]
        return mock_response_getOriginalImageFromSource;
    }

    public String save(String s3_original_url, ImageRequestDto imageRequestDto) throws CustomS3Exception {
        // Call to the external system to save in S3 [mocked_externalS3]
//        throw new CustomS3Exception("error");
        return mock_response_save;
    }

    public boolean flushImage(String fileURL) {
        // Call to the external system to delete from S3 [mocked_externalS3]
        return mock_response_flushImage;
    }

    public List<String> getBuckets() {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return Arrays.asList("europe", "us", "ap", "china");
    }

    public boolean doesObjectExist(String bucket, String imagePath) {
        // Call to the external system to check from S3 [mocked_externalS3]
        return mock_response_doesObjectExist;
    }

    public String optimise(String s3_original_url, ImageRequestDto imageRequestDto) {
        return mock_response_optimise;
    }
}
