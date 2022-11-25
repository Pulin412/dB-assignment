package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.model.ImageResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface ImageService {
    ImageResponseDto getImage(String preDefinedType, String seo, String reference);

    boolean flush(String preDefinedType, String reference);
}
