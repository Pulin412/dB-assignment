package com.db.assignment.image_service.service;

import com.db.assignment.image_service.exception.CustomS3Exception;
import com.db.assignment.image_service.model.ExternalImageDto;
import com.db.assignment.image_service.model.ExternalImageResponseDto;
import com.db.assignment.image_service.model.ImageRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocalStoreServiceImpl implements LocalStoreService{

    private final S3OperationService s3OperationService;

    public LocalStoreServiceImpl(S3OperationService s3OperationService) {
        this.s3OperationService = s3OperationService;
    }

    @Override
    public String createS3Url(ImageRequestDto imageRequestDto) {
        return s3OperationService.createS3Url(imageRequestDto);
    }

    @Override
    public StringBuilder getBucketPathFromFileName(String reference, StringBuilder sb) {
        return s3OperationService.getBucketPathFromFileName(reference, sb);
    }

    @Override
    public String getOriginalImageURL(String s3Url) {
        return s3OperationService.getOriginalImageURL(s3Url);
    }

    @Override
    public Optional<ExternalImageResponseDto> getOptimisedImageFromS3(ExternalImageDto externalImageDto) {
        return s3OperationService.getOptimisedImageFromS3(externalImageDto);
    }

    @Override
    public Optional<ExternalImageResponseDto> getOriginalImageFromS3(ExternalImageDto externalImageDto) {
        return s3OperationService.getOriginalImageFromS3(externalImageDto);
    }

    @Override
    public Optional<ExternalImageResponseDto> save(ExternalImageDto externalImageDto) throws CustomS3Exception {
        return s3OperationService.save(externalImageDto);
    }

    @Override
    public Optional<ExternalImageResponseDto> recover(CustomS3Exception e, ExternalImageDto externalImageDto) {
        return s3OperationService.recover(e, externalImageDto);
    }

    @Override
    public boolean flushImage(ExternalImageDto externalImageDto) {
        return s3OperationService.flushImage(externalImageDto);
    }

    @Override
    public List<String> getBuckets() {
        return s3OperationService.getBuckets();
    }

    @Override
    public boolean doesObjectExist(ExternalImageDto externalImageDto) {
        return s3OperationService.doesObjectExist(externalImageDto);
    }

    @Override
    public Optional<ExternalImageResponseDto> optimise(ExternalImageDto externalImageDto) {
        return s3OperationService.optimise(externalImageDto);
    }
}
