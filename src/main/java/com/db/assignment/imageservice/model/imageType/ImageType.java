package com.db.assignment.imageservice.model.imageType;

import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@ToString
public abstract class ImageType {

    private ImageConfigContainer imageConfigContainer;
}
