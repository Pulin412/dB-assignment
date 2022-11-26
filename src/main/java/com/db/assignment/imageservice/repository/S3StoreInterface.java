package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.model.S3ImageDto;

import java.util.List;

public interface S3StoreInterface {

    String getOptimisedImageFromS3(S3ImageDto s3ImageDto);

    String getOriginalImageFromS3(S3ImageDto s3ImageDto);

    String save(S3ImageDto s3ImageDto) throws CustomS3Exception;

    boolean flushImage(S3ImageDto s3ImageDto);

    List<String> getBuckets();

    boolean doesObjectExist(S3ImageDto s3ImageDto);

    String optimise(S3ImageDto s3ImageDto);
}
