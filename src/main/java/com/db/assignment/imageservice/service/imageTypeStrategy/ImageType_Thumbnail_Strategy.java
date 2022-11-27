package com.db.assignment.imageservice.service.imageTypeStrategy;

import com.db.assignment.imageservice.config.ImageTypes.ThumbnailConfig;
import com.db.assignment.imageservice.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.imageservice.model.imageType.ImageType;
import com.db.assignment.imageservice.model.imageType.Thumbnail_ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageType_Thumbnail_Strategy implements ImageTypeStrategy{

    @Autowired
    private ThumbnailConfig thumbnailConfig;

    @Override
    public ImageType getImageType() {
        return Thumbnail_ImageType.builder()
                .imageConfigContainer(thumbnailConfig.getImageConfigContainer())
                .build();
    }

    @Override
    public ImageTypeStrategyNameEnum getImageTypeStrategyNameEnum() {
        return ImageTypeStrategyNameEnum.THUMBNAIL;
    }
}