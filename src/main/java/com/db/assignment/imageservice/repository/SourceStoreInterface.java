package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.model.ExternalImageDto;

public interface SourceStoreInterface {

    String getOriginalImageFromSource(ExternalImageDto externalImageDto);
}
