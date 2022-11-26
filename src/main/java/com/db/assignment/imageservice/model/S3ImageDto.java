package com.db.assignment.imageservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class S3ImageDto {

    private String s3ObjectUrl;
    private ImageRequestDto imageRequestDto;
    private String bucket;
}
