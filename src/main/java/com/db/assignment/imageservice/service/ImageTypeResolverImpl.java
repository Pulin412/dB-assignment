package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.config.ImageTypes.DetailLargeConfig;
import com.db.assignment.imageservice.config.ImageTypes.PortraitConfig;
import com.db.assignment.imageservice.config.ImageTypes.ThumbnailConfig;
import com.db.assignment.imageservice.exception.GenericException;
import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.enums.PreDefImageTypesEnum;
import com.db.assignment.imageservice.model.imageType.DetailLarge_ImageType;
import com.db.assignment.imageservice.model.imageType.ImageType;
import com.db.assignment.imageservice.model.imageType.Portrait_ImageType;
import com.db.assignment.imageservice.model.imageType.Thumbnail_ImageType;
import com.db.assignment.imageservice.utils.ImageServiceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ImageTypeResolverImpl implements ImageTypeResolver{

    private static final Logger log = LoggerFactory.getLogger(ImageTypeResolverImpl.class);

    @Override
    public ImageType createImageType(ImageRequestDto imageRequestDto, String imageType, ThumbnailConfig thumbnailConfig, DetailLargeConfig detailLargeConfig, PortraitConfig portraitConfig) {
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
            throw new GenericException(ImageServiceConstants.EXCEPTION_MESSAGE_INVALID_PRE_DEFINED_TYPE);
        }
    }
}
