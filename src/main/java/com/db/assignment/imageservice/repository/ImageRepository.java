package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.model.ImageRequestDto;
import org.springframework.stereotype.Repository;

@Repository
public class ImageRepository {

    public String getOptimisedImageFromS3(String s3Url) {
        return null;
    }

    public String getOriginalImageFromS3(String s3Url) {
        return null;
    }

    public String getOriginalImageFromSource(ImageRequestDto imageRequestDto) {
        return "~/dir1/dir2/sourceImage";
    }

    public String compressAndSave(String s3_original_url, ImageRequestDto imageRequestDto) throws CustomS3Exception {
        throw new CustomS3Exception("error");
//        return "saved";
    }
}
