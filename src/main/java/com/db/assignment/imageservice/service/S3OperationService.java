package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.model.ImageRequestDto;

public interface S3OperationService {

    String createS3Url(ImageRequestDto imageRequestDto);
    StringBuilder getBucketPathFromFileName(String reference, StringBuilder sb);
    String getOriginalImageURL(String s3Url);
}
