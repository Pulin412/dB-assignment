package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.model.ExternalImageDto;
import com.db.assignment.imageservice.model.ExternalImageResponseDto;
import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.ImageResponseDto;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.IOException;
import java.util.Optional;

public interface ImageService {

    ImageResponseDto getImage(ImageRequestDto imageRequestDto) throws IOException;
    boolean flush(String preDefinedType, String reference);
}
