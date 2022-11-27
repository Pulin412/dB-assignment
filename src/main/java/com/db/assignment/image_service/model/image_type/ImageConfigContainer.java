package com.db.assignment.image_service.model.image_type;

import com.db.assignment.image_service.model.enums.ImageExtensionEnum;
import com.db.assignment.image_service.model.enums.ScaleTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ImageConfigContainer {
    private int height;
    private int width;
    private int quality;
    private ScaleTypeEnum scaleType;
    private String fillColor;
    private ImageExtensionEnum imageExtension;
}
