package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.config.ImageTypes.DetailLargeConfig;
import com.db.assignment.imageservice.config.ImageTypes.PortraitConfig;
import com.db.assignment.imageservice.config.ImageTypes.ThumbnailConfig;
import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.exception.ImageNotFoundException;
import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.ImageResponseDto;
import com.db.assignment.imageservice.model.enums.PreDefImageTypesEnum;
import com.db.assignment.imageservice.model.imageType.DetailLarge_ImageType;
import com.db.assignment.imageservice.model.imageType.ImageType;
import com.db.assignment.imageservice.model.imageType.Portrait_ImageType;
import com.db.assignment.imageservice.model.imageType.Thumbnail_ImageType;
import com.db.assignment.imageservice.repository.ImageRepository;
import com.db.assignment.imageservice.utils.ImageServiceConstants;
import com.db.assignment.imageservice.utils.ImageServiceUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public ImageResponseDto getImage(ImageRequestDto imageRequestDto) {

        // 1. Validate the incoming request
        validate(imageRequestDto);
        log.info("IMAGE_SERVICE ::::: show ::::: validate ::::: Incoming request validated");

        String optimised_Object_From_S3_Url = "";
        String s3_Optimised_Url ="";
        String original_Object_Url = "";

        try {
            // 2. Create the S3 URL to access/store the optimised image
            String object_For_S3_Url = ImageServiceUtils.createS3Url(imageRequestDto);
            log.info("IMAGE_SERVICE ::::: show ::::: Fetching Compressed Image from S3 located at - " + object_For_S3_Url);

            //3. Get the optimised image from S3 using the URL created at step 2
            optimised_Object_From_S3_Url = imageRepository.getOptimisedImageFromS3(object_For_S3_Url);

            // 3a. Optimised image IS present, create and send the response to client
            if(Strings.isNotEmpty(optimised_Object_From_S3_Url)){
                log.info("IMAGE_SERVICE ::::: show ::::: Found compressed Image at [" + optimised_Object_From_S3_Url + "]... Returning to client");
                return ImageResponseDto.builder()
                        .s3BucketUrl(optimised_Object_From_S3_Url)
                        .build();
            }

            // 3b. Optimised image IS NOT present
            // 4. Get the original image from S3 using the same URL created in step 2
            String original_Object_For_S3_Url = ImageServiceUtils.getOriginalImageURL(object_For_S3_Url);
            log.info("IMAGE_SERVICE ::::: show ::::: Compressed Image not Present in S3, checking Original Image in S3 at - " + original_Object_For_S3_Url);
            original_Object_Url = imageRepository.getOriginalImageFromS3(original_Object_For_S3_Url);

            // 4a. Original image IS NOT present in S3, download image from the source
            if(Strings.isEmpty(original_Object_Url)){
                log.info("IMAGE_SERVICE ::::: show ::::: Original Image not found in S3, fetching from the source");
                original_Object_Url = imageRepository.getOriginalImageFromSource(imageRequestDto);
                if(Strings.isNotEmpty(original_Object_Url))
                    log.info("IMAGE_SERVICE ::::: show ::::: Found Original Image at source located at - " + original_Object_Url);
                else {
                    log.error("IMAGE_SERVICE ::::: show ::::: Unable to find Original Image at source.");
                    throw new ImageNotFoundException(ImageServiceConstants.EXCEPTION_MESSAGE_IMAGE_NOT_PRESENT_AT_SOURCE);
                }
            }else {
                log.info("IMAGE_SERVICE ::::: show ::::: Found Original Image in S3 at - " + original_Object_Url);
            }

            //5. Optimise the fetched image from the source/s3
            log.info("IMAGE_SERVICE ::::: show ::::: Compressing the Original Image - " + original_Object_Url);
            s3_Optimised_Url = imageRepository.optimise(original_Object_Url, imageRequestDto);

        } catch (Exception ex){
            log.error("IMAGE_SERVICE ::::: show ::::: System issues, Image not found. Try again later");
            throw new ImageNotFoundException(ImageServiceConstants.EXCEPTION_MESSAGE_IMAGE_NOT_PRESENT_AT_SOURCE);
        }

        //5. Optimise the fetched image from the source and store in s3 storage.
        log.info("IMAGE_SERVICE ::::: show ::::: Saving Original Image in S3 at - " + s3_Optimised_Url);
        s3_Optimised_Url = imageRepository.save(s3_Optimised_Url, imageRequestDto);

        //6. Return the same optimised image back to the client.
        log.info("IMAGE_SERVICE ::::: show ::::: Compressed image saved successfully, returning to the client");
        return ImageResponseDto.builder()
                .s3BucketUrl(s3_Optimised_Url)
                .build();
    }

    @Recover
    public ImageResponseDto recover(CustomS3Exception e, ImageRequestDto imageRequestDto){
        log.error("IMAGE_SERVICE ::::: show ::::: Issue in connecting with external systems, quitting..");
        return ImageResponseDto.builder().build();
    }

    @Override
    public boolean flush(String preDefinedType, String reference) {

        // 1. Validate the incoming request
        validateObject(preDefinedType, reference);
        log.info("IMAGE_SERVICE ::::: flush :::: validate ::::: Incoming request validated");

        StringBuilder objectPath = new StringBuilder("/" + preDefinedType + "/");

        /* check preDefinedType:
                'Original' : Find with file name in the buckets
                Others : Create the bucket name using file Name and pass the URL for deletion.
         */
        String imagePath = ImageServiceUtils.getBucketPathFromFileName(reference, objectPath).toString();
        log.info("IMAGE_SERVICE ::::: flush ::::: Flushing image with path - " + imagePath);

        try{
            if(!preDefinedType.equalsIgnoreCase("original")){
                log.info("IMAGE_SERVICE ::::: flush ::::: Flushing compressed image with preDefinedType - " + preDefinedType);
                return imageRepository.flushImage(imagePath);
            } else {
                log.info("IMAGE_SERVICE ::::: flush ::::: Finding all optimised images");
                // 1. List all buckets
                List<String> buckets = imageRepository.getBuckets();

                // 2. Iterate through all buckets and search for created file Name as per AWS S3 directory strategy
                // 3. Delete the contents if match is present during iteration
                for (String bucket : buckets) {
                    log.info("IMAGE_SERVICE ::::: flush ::::: Finding the optimised images in bucket - " + bucket);
                    if(imageRepository.doesObjectExist(bucket, imagePath)) {
                        imageRepository.flushImage(imagePath);
                        log.info("IMAGE_SERVICE ::::: flush ::::: Found optimised image {}, flushed", imagePath);
                    }
                }
            }
        } catch (Exception ex){
            log.error("IMAGE_SERVICE ::::: flush ::::: System issue while deleting image(s). Try again later");
            throw new ImageNotFoundException(ImageServiceConstants.EXCEPTION_MESSAGE_FLUSH);
        }

        return true;
    }

    private void validateObject(String preDefinedType, String reference) {
        Optional<PreDefImageTypesEnum> optionalMatch = Arrays.stream(PreDefImageTypesEnum.values())
                .filter(val -> val.name().equalsIgnoreCase((preDefinedType)))
                .findFirst();

        if(optionalMatch.isEmpty()){
            log.error("IMAGE_SERVICE ::::: validate :::::: " + preDefinedType + " not valid");
            throw new ImageNotFoundException(ImageServiceConstants.EXCEPTION_MESSAGE_VALIDATE_PRE_DEFINED_TYPE);
        }

        //validate reference is present; add a regex to validate the pattern
        if(reference == null) {
            log.error("IMAGE_SERVICE ::::: validate :::::: file Name not valid");
            throw new ImageNotFoundException(ImageServiceConstants.EXCEPTION_MESSAGE_VALIDATE_REFERENCE);
        }

    }

    private void validate(ImageRequestDto imageRequestDto){

        //validate and create preDefinedImageType object
        imageRequestDto.setImageType(createImageType(imageRequestDto, imageRequestDto.getPreDefinedType()));
        log.info("IMAGE SERVICE :::::: validate :::::: Set predefined Image Type with details - " + imageRequestDto.getImageType());

        //validate reference is present; add a regex to validate the pattern
        if(imageRequestDto.getReference() == null) {
            log.error("IMAGE_SERVICE ::::: validate :::::: file Name not valid");
            throw new ImageNotFoundException(ImageServiceConstants.EXCEPTION_MESSAGE_VALIDATE_REFERENCE);
        }
    }

    private ImageType createImageType(ImageRequestDto imageRequestDto, String imageType){
        return ImageServiceUtils.createImageType(imageRequestDto, imageType, thumbnailConfig, detailLargeConfig, portraitConfig);
    }
}
