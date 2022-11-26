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

    public String getOptimisedImageFromS3(String s3Url) {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return null;
    }

    public String getOriginalImageFromS3(String s3Url) {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return null;
    }

    public String getOriginalImageFromSource(ImageRequestDto imageRequestDto) {
        // Call to the external system to fetch from source [mocked_external_source]
        return "~/dir1/dir2/sourceImage";
    }

    public String save(String s3_original_url, ImageRequestDto imageRequestDto) throws CustomS3Exception {
        // Call to the external system to save in S3 [mocked_externalS3]
//        throw new CustomS3Exception("error");
        return "/buckets/optimised/saved/here";
    }

    public boolean flushImage(String fileURL) {
        // Call to the external system to delete from S3 [mocked_externalS3]
        return true;
    }

    public List<String> getBuckets() {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return Arrays.asList("europe", "us", "ap", "china");
    }

    public boolean doesObjectExist(String bucket, String imagePath) {
        // Call to the external system to check from S3 [mocked_externalS3]
        return true;
    }

    public String optimise(String s3_original_url, ImageRequestDto imageRequestDto) {
        return "/buckets/optimised/";
    }
}
