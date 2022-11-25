package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.ImageResponseDto;

import java.io.IOException;

public interface ImageService {
    ImageResponseDto getImage(ImageRequestDto imageRequestDto) throws IOException;

    boolean flush(String preDefinedType, String reference);
}
