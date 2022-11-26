package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.model.ExternalImageDto;

import java.util.List;

public interface S3StoreRepo {

    String getOptimisedImageFromS3(ExternalImageDto externalImageDto);

    String getOriginalImageFromS3(ExternalImageDto externalImageDto);

    String save(ExternalImageDto externalImageDto) throws CustomS3Exception;

    boolean flushImage(ExternalImageDto externalImageDto);

    List<String> getBuckets();

    boolean doesObjectExist(ExternalImageDto externalImageDto);

    String optimise(ExternalImageDto externalImageDto);
}
