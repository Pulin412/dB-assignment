package com.db.assignment.image_service.service;

import com.db.assignment.image_service.model.ImageRequestDto;
import com.db.assignment.image_service.model.ImageResponseDto;

import java.io.IOException;

public interface ImageService {

    ImageResponseDto getImage(ImageRequestDto imageRequestDto) throws IOException;
    boolean flush(String preDefinedType, String reference);
}
