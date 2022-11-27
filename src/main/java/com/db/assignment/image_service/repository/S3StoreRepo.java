package com.db.assignment.image_service.repository;

import com.db.assignment.image_service.exception.CustomS3Exception;
import com.db.assignment.image_service.model.ExternalImageDto;
import com.db.assignment.image_service.model.ExternalImageResponseDto;

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
