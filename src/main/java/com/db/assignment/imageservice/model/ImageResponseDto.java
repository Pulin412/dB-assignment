package com.db.assignment.imageservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ImageResponseDto {

    private UUID imageId;
    private String s3BucketUrl;
}
