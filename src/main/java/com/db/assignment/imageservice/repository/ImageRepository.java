package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.ImageResponseDto;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
public class ImageRepository {

    public ImageResponseDto fetchFromS3(String url){
        return null;
    }

    public String getOptimisedImage(ImageRequestDto imageRequestDto) {
        return null;
    }

    public String getOriginalImageFromS3(ImageRequestDto imageRequestDto) {
        return null;
    }

    public String getOriginalImageFromSource(ImageRequestDto imageRequestDto) {
        return null;
    }

    public String compressAndSave(String s3_original_url, ImageRequestDto imageRequestDto) throws IOException {
        return null;
    }
}
