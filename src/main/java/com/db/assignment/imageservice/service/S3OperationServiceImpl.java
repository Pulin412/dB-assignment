package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.enums.PreDefImageTypesEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class S3OperationServiceImpl implements S3OperationService{

    private static final Logger log = LoggerFactory.getLogger(S3OperationServiceImpl.class);

    /*
        Utility Method to create URL to match the AWS directory strategy
        to fetch/store original and compressed images.
     */
    @Override
    public String createS3Url(ImageRequestDto imageRequestDto) {
        String preDefinedType = imageRequestDto.getPreDefinedType();
        String reference = imageRequestDto.getReference();
        StringBuilder sb = new StringBuilder("~/");

        Optional<PreDefImageTypesEnum> optionalMatch = Arrays.stream(PreDefImageTypesEnum.values())
                .filter(val -> val.name().equalsIgnoreCase((imageRequestDto.getPreDefinedType())))
                .findFirst();

        if(optionalMatch.isEmpty())
            preDefinedType = PreDefImageTypesEnum.ORIGINAL.name().toLowerCase();

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
        return s3Url.replace(preDefType, PreDefImageTypesEnum.ORIGINAL.name().toLowerCase());
    }
}
