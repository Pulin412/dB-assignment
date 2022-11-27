package com.db.assignment.imageservice.service.imageTypeStrategy;

import com.db.assignment.imageservice.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.imageservice.model.imageType.ImageType;

public interface ImageTypeStrategy {

    ImageType getImageType();
    ImageTypeStrategyNameEnum getImageTypeStrategyNameEnum();
}