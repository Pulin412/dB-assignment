package com.db.assignment.imageservice.externalGateway;

import com.db.assignment.imageservice.model.ExternalImageResponseDto;
import com.db.assignment.imageservice.model.ExternalImageDto;

public interface ImageGatewayService {

    ExternalImageResponseDto fetchImage(ExternalImageDto externalImageDto);
}
