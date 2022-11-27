package com.db.assignment.image_service.model;

import com.db.assignment.image_service.model.image_type.ImageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ImageRequestDto {

    private String preDefinedType;
    private String seo;
    private String reference;
    private ImageMetaData imageMetaData;
    private ImageType imageType;
}
