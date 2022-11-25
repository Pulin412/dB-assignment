package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.ImageResponseDto;

public interface ImageService {
    ImageResponseDto getImage(ImageRequestDto imageRequestDto);

    boolean flush(String preDefinedType, String reference);
}
