package com.db.assignment.image_service.service.image_type_strategy;

import com.db.assignment.image_service.config.Image_types.PortraitConfig;
import com.db.assignment.image_service.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.image_service.model.image_type.ImageType;
import com.db.assignment.image_service.model.image_type.PortraitImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageTypePortraitStrategy implements ImageTypeStrategy{

    @Autowired
    private PortraitConfig portraitConfig;

    @Override
    public ImageType getImageType() {
        return PortraitImageType.builder()
                .imageConfigContainer(portraitConfig.getImageConfigContainer())
                .build();
    }

    @Override
    public ImageTypeStrategyNameEnum getImageTypeStrategyNameEnum() {
        return ImageTypeStrategyNameEnum.PORTRAIT;
    }
}
