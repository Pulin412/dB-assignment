package com.db.assignment.imageservice.utils;

import com.db.assignment.imageservice.config.ImageTypes.DetailLargeConfig;
import com.db.assignment.imageservice.config.ImageTypes.PortraitConfig;
import com.db.assignment.imageservice.config.ImageTypes.ThumbnailConfig;
import com.db.assignment.imageservice.exception.ImageNotFoundException;
import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.enums.PreDefImageTypesEnum;
import com.db.assignment.imageservice.model.imageType.DetailLarge_ImageType;
import com.db.assignment.imageservice.model.imageType.ImageType;
import com.db.assignment.imageservice.model.imageType.Portrait_ImageType;
import com.db.assignment.imageservice.model.imageType.Thumbnail_ImageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Optional;

public class ImageServiceUtils {

    private static final Logger log = LoggerFactory.getLogger(ImageServiceUtils.class);

    /*
        Utility Method to create URL to match the AWS directory strategy
        to fetch/store original and compressed images.
     */
    public static String createS3Url(ImageRequestDto imageRequestDto) {
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

    public static StringBuilder getBucketPathFromFileName(String reference, StringBuilder sb){
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
    public static String getOriginalImageURL(String s3Url) {
        String[] arr = s3Url.split("/");
        String preDefType = arr[1];
        return s3Url.replace(preDefType, PreDefImageTypesEnum.ORIGINAL.name().toLowerCase());
    }

    public static ImageType createImageType(ImageRequestDto imageRequestDto, String imageType, ThumbnailConfig thumbnailConfig, DetailLargeConfig detailLargeConfig, PortraitConfig portraitConfig) {
        if(imageType.equalsIgnoreCase(PreDefImageTypesEnum.THUMBNAIL.name().toLowerCase()))
            return Thumbnail_ImageType.builder()
                    .height(thumbnailConfig.getHeight())
                    .width(thumbnailConfig.getWidth())
                    .quality(thumbnailConfig.getQuality())
                    .fillColor(thumbnailConfig.getFillColor())
                    .scaleType(thumbnailConfig.getScaleType())
                    .imageExtension(thumbnailConfig.getImageExtension())
                    .build();
        else  if(imageType.equalsIgnoreCase(PreDefImageTypesEnum.DETAILLARGE.name().toLowerCase()))
            return DetailLarge_ImageType.builder()
                    .height(detailLargeConfig.getHeight())
                    .width(detailLargeConfig.getWidth())
                    .quality(detailLargeConfig.getQuality())
                    .fillColor(detailLargeConfig.getFillColor())
                    .scaleType(detailLargeConfig.getScaleType())
                    .imageExtension(detailLargeConfig.getImageExtension())
                    .build();
        else  if(imageType.equalsIgnoreCase(PreDefImageTypesEnum.PORTRAIT.name().toLowerCase()))
            return Portrait_ImageType.builder()
                    .height(portraitConfig.getHeight())
                    .width(portraitConfig.getWidth())
                    .quality(portraitConfig.getQuality())
                    .fillColor(portraitConfig.getFillColor())
                    .scaleType(portraitConfig.getScaleType())
                    .imageExtension(portraitConfig.getImageExtension())
                    .build();
        else{
            log.error("IMAGE_SERVICE ::::: Pre defined type {} not valid", imageRequestDto.getPreDefinedType());
            throw new ImageNotFoundException(ImageServiceConstants.EXCEPTION_MESSAGE_IMAGE_NOT_FOUND);
        }
    }
}
