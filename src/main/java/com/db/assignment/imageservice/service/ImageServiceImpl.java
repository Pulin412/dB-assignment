package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.config.ImageTypes.DetailLargeConfig;
import com.db.assignment.imageservice.config.ImageTypes.PortraitConfig;
import com.db.assignment.imageservice.config.ImageTypes.ThumbnailConfig;
import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.exception.ImageNotFoundException;
import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.ImageResponseDto;
import com.db.assignment.imageservice.model.imageType.DetailLarge_ImageType;
import com.db.assignment.imageservice.model.imageType.ImageType;
import com.db.assignment.imageservice.model.imageType.Portrait_ImageType;
import com.db.assignment.imageservice.model.imageType.Thumbnail_ImageType;
import com.db.assignment.imageservice.repository.ImageRepository;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ImageServiceImpl implements ImageService{

    private final ImageRepository imageRepository;
    private final ThumbnailConfig thumbnailConfig;
    private final PortraitConfig portraitConfig;
    private final DetailLargeConfig detailLargeConfig;
    private final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    public ImageServiceImpl(ImageRepository imageRepository, ThumbnailConfig thumbnailConfig, PortraitConfig portraitConfig, DetailLargeConfig detailLargeConfig) {
        this.imageRepository = imageRepository;
        this.thumbnailConfig = thumbnailConfig;
        this.portraitConfig = portraitConfig;
        this.detailLargeConfig = detailLargeConfig;
    }

    @Override
    @Retryable(retryFor = CustomS3Exception.class, maxAttemptsExpression = "${retry.maxAttempts}",
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

        return ImageResponseDto.builder()
                .s3BucketUrl(s3_Optimised_Url)
                .build();
    }

    @Recover
    public ImageResponseDto recover(CustomS3Exception e, ImageRequestDto imageRequestDto){
        log.error("IMAGE_SERVICE ::::: Issue in connecting with external systems, quitting..");
        return ImageResponseDto.builder().build();
    }

    @Override
    public boolean flush(String preDefinedType, String reference) {
        return false;
    }

    private void validate(ImageRequestDto imageRequestDto){

        //validate and create preDefinedImageType object
        imageRequestDto.setImageType(createImageType(imageRequestDto, imageRequestDto.getPreDefinedType()));
        log.info("IMAGE SERVICE :::::: Set predefined Image Type with details - " + imageRequestDto.getImageType());

        //validate reference is present; add a regex to validate the pattern
        if(imageRequestDto.getReference() == null) {
            log.info("IMAGE_SERVICE ::::: " + imageRequestDto.getReference() + " not valid");
            throw new ImageNotFoundException("Reference not found");
        }
    }

    private ImageType createImageType(ImageRequestDto imageRequestDto, String imageType){

        if(imageType.equalsIgnoreCase("thumbnail"))
            return Thumbnail_ImageType.builder()
                    .height(thumbnailConfig.getHeight())
                    .width(thumbnailConfig.getWidth())
                    .quality(thumbnailConfig.getQuality())
                    .fillColor(thumbnailConfig.getFillColor())
                    .scaleType(thumbnailConfig.getScaleType())
                    .imageExtension(thumbnailConfig.getImageExtension())
                    .build();
        else  if(imageType.equalsIgnoreCase("detail-large"))
            return DetailLarge_ImageType.builder()
                    .height(detailLargeConfig.getHeight())
                    .width(detailLargeConfig.getWidth())
                    .quality(detailLargeConfig.getQuality())
                    .fillColor(detailLargeConfig.getFillColor())
                    .scaleType(detailLargeConfig.getScaleType())
                    .imageExtension(detailLargeConfig.getImageExtension())
                    .build();
        else  if(imageType.equalsIgnoreCase("portrait"))
            return Portrait_ImageType.builder()
                    .height(portraitConfig.getHeight())
                    .width(portraitConfig.getWidth())
                    .quality(portraitConfig.getQuality())
                    .fillColor(portraitConfig.getFillColor())
                    .scaleType(portraitConfig.getScaleType())
                    .imageExtension(portraitConfig.getImageExtension())
                    .build();
        else{
            log.info("IMAGE_SERVICE ::::: " + imageRequestDto.getPreDefinedType() + " not valid");
            throw new ImageNotFoundException("Image not found");
        }
    }
}
