package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.model.ImageDto;

public interface SourceStoreInterface {

    String getOriginalImageFromSource(ImageDto imageDto);
}
