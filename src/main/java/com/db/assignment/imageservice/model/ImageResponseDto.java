package com.db.assignment.imageservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageResponseDto {

    private String s3BucketUrl;
}
