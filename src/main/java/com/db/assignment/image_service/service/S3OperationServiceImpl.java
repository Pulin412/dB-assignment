package com.db.assignment.image_service.service;

import com.db.assignment.image_service.exception.CustomS3Exception;
import com.db.assignment.image_service.exception.ImageNotFoundException;
import com.db.assignment.image_service.model.ExternalImageDto;
import com.db.assignment.image_service.model.ExternalImageResponseDto;
import com.db.assignment.image_service.model.ImageRequestDto;
import com.db.assignment.image_service.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.image_service.repository.S3StoreRepo;
import com.db.assignment.image_service.utils.ImageServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class S3OperationServiceImpl implements S3OperationService{

    private final S3StoreRepo s3StoreRepo;

    private static final Logger log = LoggerFactory.getLogger(S3OperationServiceImpl.class);

    public S3OperationServiceImpl(S3StoreRepo s3StoreRepo) {
        this.s3StoreRepo = s3StoreRepo;
    }

    /*
        Utility Method to create URL to match the AWS directory strategy
        to fetch/store original and compressed images.
     */
    @Override
    public String createS3Url(ImageRequestDto imageRequestDto) {
        String preDefinedType = imageRequestDto.getPreDefinedType();
        String reference = imageRequestDto.getReference();
        StringBuilder sb = new StringBuilder("~/");

        Optional<ImageTypeStrategyNameEnum> optionalMatch = Arrays.stream(ImageTypeStrategyNameEnum.values())
                .filter(val -> val.name().equalsIgnoreCase((imageRequestDto.getPreDefinedType())))
                .findFirst();

        if(optionalMatch.isEmpty())
            preDefinedType = ImageTypeStrategyNameEnum.ORIGINAL.name().toLowerCase();

        sb.append(preDefinedType);
        sb.append("/");

        return getBucketPathFromFileName(reference, sb).toString();
    }

    @Override
    public StringBuilder getBucketPathFromFileName(String reference, StringBuilder sb){
        String storedReference = reference.replace('/', '_');
        String fileName = storedReference.substring(0, storedReference.indexOf('.'));

        if(fileName.length() >= 8){
            sb.append(fileName.substring(0, 4));
            sb.append("/");
            sb.append(fileName.substring(4, 8));
            sb.append("/");
        }

        if(fileName.length() > 4 && fileName.length() < 8){
            sb.append(fileName.substring(0, 4));
            sb.append("/");
        }

        sb.append(storedReference);
        return sb;
    }

    /*
        Utility method to get the AWS directory URL for the original image
        to be fetched from S3.
     */
    @Override
    public String getOriginalImageURL(String s3Url) {
        String[] arr = s3Url.split("/");
        String preDefType = arr[1];
        return s3Url.replace(preDefType, ImageTypeStrategyNameEnum.ORIGINAL.name().toLowerCase());
    }

    public Optional<ExternalImageResponseDto> getOptimisedImageFromS3(ExternalImageDto externalImageDto) {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return s3StoreRepo.getOptimisedImageFromS3(externalImageDto);
    }

    @Override
    public Optional<ExternalImageResponseDto> getOriginalImageFromS3(ExternalImageDto externalImageDto) {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return s3StoreRepo.getOriginalImageFromS3(externalImageDto);
    }

    @Override
    public Optional<ExternalImageResponseDto> save(ExternalImageDto externalImageDto) throws CustomS3Exception {
        // Call to the external system to save in S3 [mocked_externalS3]
//        throw new CustomS3Exception("error");
        return s3StoreRepo.save(externalImageDto);
    }

    @Override
    public boolean flushImage(ExternalImageDto externalImageDto) {
        // Call to the external system to delete from S3 [mocked_externalS3]
        return s3StoreRepo.flushImage(externalImageDto);
    }

    @Override
    public List<String> getBuckets() {
        // Call to the external system to fetch from S3 [mocked_externalS3]
        return s3StoreRepo.getBuckets();
    }

    @Override
    public boolean doesObjectExist(ExternalImageDto externalImageDto) {
        // Call to the external system to check from S3 [mocked_externalS3]
        return s3StoreRepo.doesObjectExist(externalImageDto);
    }

    @Override
    public Optional<ExternalImageResponseDto> optimise(ExternalImageDto externalImageDto) {
        return s3StoreRepo.optimise(externalImageDto);
    }

    @Override
    public Optional<ExternalImageResponseDto>  recover(CustomS3Exception e, ExternalImageDto externalImageDto){
        log.error("IMAGE_SERVICE ::::: getImage ::::: Issue in connecting with external systems, quitting..");
        throw new ImageNotFoundException(ImageServiceConstants.EXCEPTION_MESSAGE_IMAGE_NOT_FOUND);
    }
}
