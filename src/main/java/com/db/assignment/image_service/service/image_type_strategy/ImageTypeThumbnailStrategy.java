package com.db.assignment.image_service.service.image_type_strategy;

import com.db.assignment.image_service.config.Image_types.ThumbnailConfig;
import com.db.assignment.image_service.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.image_service.model.image_type.ImageType;
import com.db.assignment.image_service.model.image_type.ThumbnailImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageTypeThumbnailStrategy implements ImageTypeStrategy{

    @Autowired
    private ThumbnailConfig thumbnailConfig;

    @Override
    public ImageType getImageType() {
        return ThumbnailImageType.builder()
                .imageConfigContainer(thumbnailConfig.getImageConfigContainer())
                .build();
    }

    @Override
    public ImageTypeStrategyNameEnum getImageTypeStrategyNameEnum() {
        return ImageTypeStrategyNameEnum.THUMBNAIL;
    }
}