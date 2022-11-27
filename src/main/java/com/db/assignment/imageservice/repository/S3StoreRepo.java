package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.model.ExternalImageDto;
import com.db.assignment.imageservice.model.ExternalImageResponseDto;

import java.util.List;
import java.util.Optional;

public interface S3StoreRepo {

    Optional<ExternalImageResponseDto> getOptimisedImageFromS3(ExternalImageDto externalImageDto);

    Optional<ExternalImageResponseDto> getOriginalImageFromS3(ExternalImageDto externalImageDto);

    Optional<ExternalImageResponseDto> save(ExternalImageDto externalImageDto) throws CustomS3Exception;

    boolean flushImage(ExternalImageDto externalImageDto);

    List<String> getBuckets();

    boolean doesObjectExist(ExternalImageDto externalImageDto);

    Optional<ExternalImageResponseDto> optimise(ExternalImageDto externalImageDto);
}
