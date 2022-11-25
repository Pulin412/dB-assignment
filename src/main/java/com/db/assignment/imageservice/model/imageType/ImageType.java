package com.db.assignment.imageservice.model.imageType;

import com.db.assignment.imageservice.model.enums.ImageExtensionEnum;
import com.db.assignment.imageservice.model.enums.ScaleTypeEnum;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
public abstract class ImageType {

    private int height;
    private int width;
    private int quality;
    private ScaleTypeEnum scaleType;
    private String fillColor;
    private ImageExtensionEnum imageExtension;
}
