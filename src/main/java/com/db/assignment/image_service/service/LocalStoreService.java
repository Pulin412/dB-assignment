package com.db.assignment.image_service.service;

import com.db.assignment.image_service.exception.CustomS3Exception;
import com.db.assignment.image_service.model.ExternalImageDto;
import com.db.assignment.image_service.model.ExternalImageResponseDto;
import com.db.assignment.image_service.model.ImageRequestDto;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;

import java.util.List;
import java.util.Optional;

public interface LocalStoreService {

    String createS3Url(ImageRequestDto imageRequestDto);
    StringBuilder getBucketPathFromFileName(String reference, StringBuilder sb);
    String getOriginalImageURL(String s3Url);
    Optional<ExternalImageResponseDto> getOptimisedImageFromS3(ExternalImageDto externalImageDto);

    Optional<ExternalImageResponseDto> getOriginalImageFromS3(ExternalImageDto externalImageDto);

    @Retryable(retryFor = CustomS3Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"),
            listeners = {"defaultListenerSupport"})
    Optional<ExternalImageResponseDto> save(ExternalImageDto externalImageDto) throws CustomS3Exception;

    @Recover
    Optional<ExternalImageResponseDto>  recover(CustomS3Exception e, ExternalImageDto externalImageDto);

    boolean flushImage(ExternalImageDto externalImageDto);

    List<String> getBuckets();

    boolean doesObjectExist(ExternalImageDto externalImageDto);

    Optional<ExternalImageResponseDto> optimise(ExternalImageDto externalImageDto);
}
