package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.ImageResponseDto;
import org.springframework.stereotype.Repository;

@Repository
public class ImageRepository {

    public ImageResponseDto fetchFromS3(String url){
        return null;
    }

    public String getOptimisedImageFromS3(ImageRequestDto imageRequestDto) throws Exception {
        return null;
//        throw new Exception();
    }

    public String getOriginalImageFromS3(ImageRequestDto imageRequestDto) {
        return null;
    }

    public String getOriginalImageFromSource(ImageRequestDto imageRequestDto) {
        return null;
    }

    public String compressAndSave(String s3_original_url, ImageRequestDto imageRequestDto) throws CustomS3Exception {
        throw new CustomS3Exception("error");
//        return "saved";
    }
}
