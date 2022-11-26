package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.model.ExternalImageDto;

public interface SourceStoreRepo {

    String getOriginalImageFromSource(ExternalImageDto externalImageDto);
}
