package com.db.assignment.imageservice.service.imageTypeStrategy;

import com.db.assignment.imageservice.config.ImageTypes.PortraitConfig;
import com.db.assignment.imageservice.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.imageservice.model.imageType.ImageType;
import com.db.assignment.imageservice.model.imageType.Portrait_ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageType_Portrait_Strategy implements ImageTypeStrategy{

    @Autowired
    private PortraitConfig portraitConfig;

    @Override
    public ImageType getImageType() {
        return Portrait_ImageType.builder()
                .height(portraitConfig.getHeight())
                .width(portraitConfig.getWidth())
                .quality(portraitConfig.getQuality())
                .fillColor(portraitConfig.getFillColor())
                .scaleType(portraitConfig.getScaleType())
                .imageExtension(portraitConfig.getImageExtension())
                .build();
    }

    @Override
    public ImageTypeStrategyNameEnum getImageTypeStrategyNameEnum() {
        return ImageTypeStrategyNameEnum.PORTRAIT;
    }
}
