package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.model.ExternalImageDto;
import com.db.assignment.imageservice.model.ExternalImageResponseDto;
import com.db.assignment.imageservice.model.ImageRequestDto;

import java.util.List;
import java.util.Optional;

public interface S3OperationService {

    String createS3Url(ImageRequestDto imageRequestDto);
    StringBuilder getBucketPathFromFileName(String reference, StringBuilder sb);
    String getOriginalImageURL(String s3Url);
    Optional<ExternalImageResponseDto> getOptimisedImageFromS3(ExternalImageDto externalImageDto);

    Optional<ExternalImageResponseDto> getOriginalImageFromS3(ExternalImageDto externalImageDto);

    Optional<ExternalImageResponseDto> save(ExternalImageDto externalImageDto) throws CustomS3Exception;

    boolean flushImage(ExternalImageDto externalImageDto);

    List<String> getBuckets();

    boolean doesObjectExist(ExternalImageDto externalImageDto);

    Optional<ExternalImageResponseDto> optimise(ExternalImageDto externalImageDto);
}
