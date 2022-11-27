package com.db.assignment.image_service.service.image_type_strategy;

import com.db.assignment.image_service.config.Image_types.DetailLargeConfig;
import com.db.assignment.image_service.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.image_service.model.image_type.DetailLargeImageType;
import com.db.assignment.image_service.model.image_type.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageTypeDetailLargeStrategy implements ImageTypeStrategy{

    @Autowired
    private DetailLargeConfig detailLargeConfig;

    @Override
    public ImageType getImageType() {
        return DetailLargeImageType.builder()
                .imageConfigContainer(detailLargeConfig.getImageConfigContainer())
                .build();
    }

    @Override
    public ImageTypeStrategyNameEnum getImageTypeStrategyNameEnum() {
        return ImageTypeStrategyNameEnum.DETAIL_LARGE;
    }
}
