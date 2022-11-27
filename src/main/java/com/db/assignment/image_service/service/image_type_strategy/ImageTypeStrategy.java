package com.db.assignment.image_service.service.image_type_strategy;

import com.db.assignment.image_service.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.image_service.model.image_type.ImageType;

public interface ImageTypeStrategy {

    ImageType getImageType();
    ImageTypeStrategyNameEnum getImageTypeStrategyNameEnum();
}
