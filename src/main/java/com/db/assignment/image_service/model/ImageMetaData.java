package com.db.assignment.image_service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ImageMetaData {

    private UUID imageId;
    private String imageName;
}
