package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.model.ImageRequestDto;

public interface SourceStoreInterface {

    String getOriginalImageFromSource(ImageRequestDto imageRequestDto);
}
