package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.exception.ImageNotFoundException;
import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.ImageResponseDto;
import com.db.assignment.imageservice.repository.ImageRepository;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;

@Service
public class ImageServiceImpl implements ImageService{

    private final ImageRepository imageRepository;
    private Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    @Retryable(value = IOException.class, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"),
            listeners = {"defaultListenerSupport"})
    public ImageResponseDto getImage(ImageRequestDto imageRequestDto) throws IOException {

        validate(imageRequestDto);
        String s3_Optimised_Url = "";
        String s3_Original_Url = "";

        try {
            s3_Optimised_Url = imageRepository.getOptimisedImageFromS3(imageRequestDto);

            if(Strings.isNotEmpty(s3_Optimised_Url)){
                return ImageResponseDto.builder()
                        .s3BucketUrl(s3_Optimised_Url)
                        .build();
            }

            s3_Original_Url = imageRepository.getOriginalImageFromS3(imageRequestDto);

            if(Strings.isEmpty(s3_Original_Url)){
                // download image from source
                s3_Original_Url = imageRepository.getOriginalImageFromSource(imageRequestDto);
            }

        } catch (Exception ex){
            log.error("IMAGE_SERVICE ::::: System issues, Image not found");
            throw new ImageNotFoundException("System issues, Image not found");
        }

        // compress image and store in s3
        s3_Optimised_Url = imageRepository.compressAndSave(s3_Original_Url, imageRequestDto);
        if(Strings.isEmpty(s3_Optimised_Url))
            log.warn("IMAGE_SERVICE ::::: Issue in connecting with external systems, retrying..");

        return ImageResponseDto.builder()
                .s3BucketUrl(s3_Optimised_Url)
                .build();
    }

    @Override
    public boolean flush(String preDefinedType, String reference) {
        return false;
    }

    private void validate(ImageRequestDto imageRequestDto){

        //TODO  predefined not exists
        if(imageRequestDto.getPreDefinedType() == null) {
            log.info("IMAGE_SERVICE ::::: " + imageRequestDto.getPreDefinedType() + " not valid");
            throw new ImageNotFoundException("Image not found");
        }

        //TODO  reference not exists
        if(imageRequestDto.getReference() == null) {
            log.info("IMAGE_SERVICE ::::: " + imageRequestDto.getReference() + " not valid");
            throw new ImageNotFoundException("Reference not found");
        }
    }

    @Recover
    public ImageResponseDto recover(IOException e, ImageRequestDto imageRequestDto){
        log.error("IMAGE_SERVICE ::::: Issue in connecting with external systems, quitting..");
        return ImageResponseDto.builder().build();
    }
}
