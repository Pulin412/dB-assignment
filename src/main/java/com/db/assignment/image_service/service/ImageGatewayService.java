package com.db.assignment.image_service.service;

import com.db.assignment.image_service.model.ExternalImageDto;

public interface ImageGatewayService {

    String getOriginalImageFromSource(ExternalImageDto externalImageDto);
}
