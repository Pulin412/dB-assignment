package com.db.assignment.imageservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageRequestDto {

    private String preDefinedType;
    private String seo;
    private String reference;
    private ImageMetaData imageMetaData;
}
