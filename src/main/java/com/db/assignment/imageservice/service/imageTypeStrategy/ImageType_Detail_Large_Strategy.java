package com.db.assignment.imageservice.service.imageTypeStrategy;

import com.db.assignment.imageservice.config.ImageTypes.DetailLargeConfig;
import com.db.assignment.imageservice.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.imageservice.model.imageType.DetailLarge_ImageType;
import com.db.assignment.imageservice.model.imageType.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageType_Detail_Large_Strategy implements ImageTypeStrategy{

    @Autowired
    private DetailLargeConfig detailLargeConfig;

    @Override
    public ImageType getImageType() {
        return DetailLarge_ImageType.builder()
                .height(detailLargeConfig.getHeight())
                .width(detailLargeConfig.getWidth())
                .quality(detailLargeConfig.getQuality())
                .fillColor(detailLargeConfig.getFillColor())
                .scaleType(detailLargeConfig.getScaleType())
                .imageExtension(detailLargeConfig.getImageExtension())
                .build();
    }

    @Override
    public ImageTypeStrategyNameEnum getImageTypeStrategyNameEnum() {
        return ImageTypeStrategyNameEnum.DETAIL_LARGE;
    }
}
