package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.exception.GenericException;
import com.db.assignment.imageservice.exception.ImageNotFoundException;
import com.db.assignment.imageservice.model.ExternalImageDto;
import com.db.assignment.imageservice.model.ExternalImageResponseDto;
import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.ImageResponseDto;
import com.db.assignment.imageservice.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.imageservice.model.imageType.ImageType;
import com.db.assignment.imageservice.repository.SourceStoreRepo;
import com.db.assignment.imageservice.service.imageTypeStrategy.ImageTypeStrategy;
import com.db.assignment.imageservice.service.imageTypeStrategy.ImageTypeStrategyFactory;
import com.db.assignment.imageservice.utils.ImageServiceConstants;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl implements ImageService{

    private final SourceStoreRepo sourceStoreRepo;

    //TODO -  add one more layer to localSourceService which talks to s3OperationService
    private final S3OperationService s3OperationService;
    private final ImageTypeStrategyFactory imageTypeStrategyFactory;
    private final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    public ImageServiceImpl(SourceStoreRepo sourceStoreRepo, S3OperationService s3OperationService, ImageTypeStrategyFactory imageTypeStrategyFactory) {
        this.sourceStoreRepo = sourceStoreRepo;
        this.s3OperationService = s3OperationService;
        this.imageTypeStrategyFactory = imageTypeStrategyFactory;
    }

    @Override
    public ImageResponseDto getImage(ImageRequestDto imageRequestDto) {

        String optimised_Object_From_S3_Url = "";
        String s3_Optimised_Url ="";
        String original_Object_Url = "";
        Optional<ExternalImageResponseDto> optionalExternalImageResponseDto;

        try {
            // 1. Validate the incoming request
            validate(imageRequestDto);
            log.debug("IMAGE_SERVICE ::::: getImage ::::: validate ::::: Incoming request validated");

            // 2. Create the S3 URL to access/store the optimised image
            String object_For_S3_Url = s3OperationService.createS3Url(imageRequestDto);
            log.debug("IMAGE_SERVICE ::::: getImage ::::: Fetching Compressed Image {} from S3 located at - {} ", imageRequestDto.getImageMetaData().getImageId(), object_For_S3_Url);

            //3. Get the optimised image from S3 using the URL created at step 2
            optionalExternalImageResponseDto = s3OperationService.getOptimisedImageFromS3(ExternalImageDto.builder().s3ObjectUrl(object_For_S3_Url).build());
            if(optionalExternalImageResponseDto.isPresent()){
                optimised_Object_From_S3_Url = optionalExternalImageResponseDto.get().getSourceImageUrl();
            } else {
                throw new GenericException(ImageServiceConstants.EXCEPTION_MESSAGE_GENERIC);
            }

            // 3a. Optimised image IS present, create and send the response to client
            if(Strings.isNotEmpty(optimised_Object_From_S3_Url)){
                log.debug("IMAGE_SERVICE ::::: getImage ::::: Found compressed Image {} at {} ... Returning to client", imageRequestDto.getImageMetaData().getImageId(), optimised_Object_From_S3_Url);
                return ImageResponseDto.builder()
                        .imageId(optionalExternalImageResponseDto.get().getImageId())
                        .s3BucketUrl(optimised_Object_From_S3_Url)
                        .build();
            }

            // 3b. Optimised image IS NOT present
            // 4. Get the original image from S3 using the same URL created in step 2
            String original_Object_For_S3_Url = s3OperationService.getOriginalImageURL(object_For_S3_Url);
            log.debug("IMAGE_SERVICE ::::: getImage ::::: Compressed Image {} not Present in S3, checking Original Image in S3 at {}", imageRequestDto.getImageMetaData().getImageId(), original_Object_For_S3_Url);

            optionalExternalImageResponseDto = s3OperationService.getOriginalImageFromS3(ExternalImageDto.builder().s3ObjectUrl(original_Object_For_S3_Url).build());
            if(optionalExternalImageResponseDto.isPresent()){
                original_Object_Url = optionalExternalImageResponseDto.get().getSourceImageUrl();
            } else {
                throw new GenericException(ImageServiceConstants.EXCEPTION_MESSAGE_GENERIC);
            }

            // 4a. Original image IS NOT present in S3, download image from the source
            if(Strings.isEmpty(original_Object_Url)){
                log.debug("IMAGE_SERVICE ::::: getImage ::::: Original Image {} not found in S3, fetching from the source", imageRequestDto.getImageMetaData().getImageId());
                original_Object_Url = sourceStoreRepo.getOriginalImageFromSource(ExternalImageDto.builder().imageRequestDto(imageRequestDto).build());
                if(Strings.isNotEmpty(original_Object_Url))
                    log.debug("IMAGE_SERVICE ::::: getImage ::::: Found Original Image {} at source located at {} ", imageRequestDto.getImageMetaData().getImageId(), original_Object_Url);
                else {
                    log.error("IMAGE_SERVICE ::::: getImage ::::: Unable to find Original Image at source.");
                    throw new ImageNotFoundException(ImageServiceConstants.EXCEPTION_MESSAGE_IMAGE_NOT_PRESENT_AT_SOURCE);
                }
            }else {
                log.debug("IMAGE_SERVICE ::::: getImage ::::: Found Original Image {} in S3 at {} ", imageRequestDto.getImageMetaData().getImageId(), original_Object_Url);
            }

            //5. Optimise the fetched image from the source/s3
            log.debug("IMAGE_SERVICE ::::: getImage ::::: Compressing the Original Image {} ", original_Object_Url);

            optionalExternalImageResponseDto = s3OperationService.optimise(ExternalImageDto.builder().s3ObjectUrl(original_Object_Url).imageRequestDto(imageRequestDto).build());
            if(optionalExternalImageResponseDto.isPresent()){
                s3_Optimised_Url = optionalExternalImageResponseDto.get().getSourceImageUrl();
            } else {
                throw new GenericException(ImageServiceConstants.EXCEPTION_MESSAGE_GENERIC);
            }

        } catch (ImageNotFoundException ex){
            throw new ImageNotFoundException(ex.getMessage());
        } catch (GenericException ex){
            throw new GenericException(ex.getMessage());
        } catch (Exception ex){
            log.error("IMAGE_SERVICE ::::: getImage ::::: System issues, Image not found. Try again later");
            throw new GenericException(ImageServiceConstants.EXCEPTION_MESSAGE_GENERIC);
        }

        //5. Optimise the fetched image from the source and store in s3 storage.
        log.debug("IMAGE_SERVICE ::::: getImage ::::: Saving Original Image {} in S3 at {} ", imageRequestDto.getImageMetaData().getImageId(), s3_Optimised_Url);
        optionalExternalImageResponseDto = s3OperationService.save(ExternalImageDto.builder().s3ObjectUrl(s3_Optimised_Url).imageRequestDto(imageRequestDto).build());

        if(optionalExternalImageResponseDto.isPresent()){
            s3_Optimised_Url = optionalExternalImageResponseDto.get().getSourceImageUrl();
        } else {
            throw new GenericException(ImageServiceConstants.EXCEPTION_MESSAGE_GENERIC);
        }

        //6. Return the same optimised image back to the client.
        log.info("IMAGE_SERVICE ::::: getImage ::::: Compressed image {} saved successfully, returning to the client", imageRequestDto.getImageMetaData().getImageId());
        return ImageResponseDto.builder()
                .imageId(optionalExternalImageResponseDto.get().getImageId())
                .s3BucketUrl(s3_Optimised_Url)
                .build();
    }

    @Override
    public boolean flush(String preDefinedType, String reference) {

        try{
            // 1. Validate the incoming request
            validateRequestInputs(preDefinedType, reference);
            log.debug("IMAGE_SERVICE ::::: flush :::: validate ::::: Incoming request validated");

            StringBuilder objectPath = new StringBuilder("/" + preDefinedType + "/");

            /* check preDefinedType:
                    'Original' : Find with file name in the buckets
                    Others : Create the bucket name using file Name and pass the URL for deletion.
             */
            String imagePath = s3OperationService.getBucketPathFromFileName(reference, objectPath).toString();
            log.debug("IMAGE_SERVICE ::::: flush ::::: Flushing image with path {} ", imagePath);

            if(!preDefinedType.equalsIgnoreCase(ImageTypeStrategyNameEnum.ORIGINAL.toString())){
                log.debug("IMAGE_SERVICE ::::: flush ::::: Flushing compressed image with preDefinedType {}", preDefinedType);
                return s3OperationService.flushImage(ExternalImageDto.builder().s3ObjectUrl(imagePath).build());
            } else {
                log.debug("IMAGE_SERVICE ::::: flush ::::: Finding all optimised images");
                // 1. List all buckets
                List<String> buckets = s3OperationService.getBuckets();

                // 2. Iterate through all buckets and search for created file Name as per AWS S3 directory strategy
                // 3. Delete the contents if match is present during iteration
                for (String bucket : buckets) {
                    log.debug("IMAGE_SERVICE ::::: flush ::::: Finding the optimised images in bucket {}", bucket);
                    if(s3OperationService.doesObjectExist(ExternalImageDto.builder().bucket(bucket).s3ObjectUrl(imagePath).build())) {
                        s3OperationService.flushImage(ExternalImageDto.builder().s3ObjectUrl(imagePath).build());
                        log.info("IMAGE_SERVICE ::::: flush ::::: Found optimised image {}, flushed", imagePath);
                    }
                }
            }
        } catch (Exception ex){
            log.error("IMAGE_SERVICE ::::: flush ::::: System issue while deleting image(s). Try again later");
            throw new GenericException(ImageServiceConstants.EXCEPTION_MESSAGE_FLUSH);
        }

        return true;
    }

    private void validateRequestInputs(String preDefinedType, String reference) {
        Optional<ImageTypeStrategyNameEnum> optionalMatch = Arrays.stream(ImageTypeStrategyNameEnum.values())
                .filter(val -> val.name().equalsIgnoreCase((preDefinedType)))
                .findFirst();

        if(optionalMatch.isEmpty()){
            log.error("IMAGE_SERVICE ::::: validate :::::: {} not valid", preDefinedType);
            throw new GenericException(ImageServiceConstants.EXCEPTION_MESSAGE_INVALID_PRE_DEFINED_TYPE);
        }

        //validate reference is present; add a regex to validate the pattern
        if(Strings.isEmpty(reference)) {
            log.error("IMAGE_SERVICE ::::: validate :::::: file Name not valid");
            throw new GenericException(ImageServiceConstants.EXCEPTION_MESSAGE_VALIDATE_REFERENCE);
        }

    }

    private void validate(ImageRequestDto imageRequestDto){

        //validate and create preDefinedImageType object
        validateRequestInputs(imageRequestDto.getPreDefinedType(), imageRequestDto.getReference());

        imageRequestDto.setImageType(createImageType(imageRequestDto.getPreDefinedType()));
        log.debug("IMAGE SERVICE :::::: validate :::::: Set predefined Image Type with details {} ", imageRequestDto.getImageType());
    }

    private ImageType createImageType(String imageType){

        ImageTypeStrategy imageTypeStrategy = imageTypeStrategyFactory.findStrategy(ImageTypeStrategyNameEnum.valueOf(imageType.toUpperCase()));
        if(imageTypeStrategy == null){
            throw new GenericException(ImageServiceConstants.EXCEPTION_MESSAGE_GENERAL);
        }
        return imageTypeStrategy.getImageType();
    }
}
