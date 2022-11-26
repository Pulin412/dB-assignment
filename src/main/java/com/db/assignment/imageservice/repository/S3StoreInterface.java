package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.exception.CustomS3Exception;
import com.db.assignment.imageservice.model.ImageDto;

import java.util.List;

public interface S3StoreInterface {

    String getOptimisedImageFromS3(ImageDto imageDto);

    String getOriginalImageFromS3(ImageDto imageDto);

    String save(ImageDto imageDto) throws CustomS3Exception;

    boolean flushImage(ImageDto imageDto);

    List<String> getBuckets();

    boolean doesObjectExist(ImageDto imageDto);

    String optimise(ImageDto imageDto);
}
