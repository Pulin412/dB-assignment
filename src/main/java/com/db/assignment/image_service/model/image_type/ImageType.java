package com.db.assignment.image_service.model.image_type;

import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
public abstract class ImageType {

    private ImageConfigContainer imageConfigContainer;
}
