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
import com.db.assignment.imageservice.utils.ImageServiceUtils;
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

        // 1. Validate the incoming request
        validate(imageRequestDto);

        String s3_Optimised_Url = "";
        String s3_Original_Url = "";

        try {
            // 2. Create the S3 URL to access/store the optimised image
            String s3Url = ImageServiceUtils.createS3Url(imageRequestDto);

            //3. Get the optimised image from S3 using the URL created at step 2
            s3_Optimised_Url = imageRepository.getOptimisedImageFromS3(s3Url);

            // 3a. Optimised image IS present, create and send the response to client
            if(Strings.isNotEmpty(s3_Optimised_Url)){
                return ImageResponseDto.builder()
                        .s3BucketUrl(s3_Optimised_Url)
                        .build();
            }

            // 3b. Optimised image IS NOT present
            // 4. Get the original image from S3 using the same URL created in step 2
            String s3OriginalUrl = ImageServiceUtils.getOriginalImageURL(s3Url);
            System.out.println(s3OriginalUrl);
            s3_Original_Url = imageRepository.getOriginalImageFromS3(s3Url);

            // 4a. Original image IS NOT present in S3, download image from the source
            if(Strings.isEmpty(s3_Original_Url)){
                s3_Original_Url = imageRepository.getOriginalImageFromSource(imageRequestDto);
            }
        } catch (Exception ex){
            log.error("IMAGE_SERVICE ::::: System issues, Image not found");
            throw new ImageNotFoundException("System issues, Image not found");
        }

        //5. Optimise the fetched image from the source and store in s3 storage.
        s3_Optimised_Url = imageRepository.compressAndSave(s3_Original_Url, imageRequestDto);

        //6. Return the same optimised image back to the client.
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
        return ImageServiceUtils.createImageType(imageRequestDto, imageType, thumbnailConfig, detailLargeConfig, portraitConfig);
    }
}
