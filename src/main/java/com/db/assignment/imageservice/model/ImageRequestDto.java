package com.db.assignment.imageservice.model;

import com.db.assignment.imageservice.model.imageType.ImageType;
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
