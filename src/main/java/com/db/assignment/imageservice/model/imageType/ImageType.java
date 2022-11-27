package com.db.assignment.imageservice.model.imageType;

import com.db.assignment.imageservice.model.enums.ImageExtensionEnum;
import com.db.assignment.imageservice.model.enums.ScaleTypeEnum;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
public abstract class ImageType {

    private ImageConfigContainer imageConfigContainer;
}
