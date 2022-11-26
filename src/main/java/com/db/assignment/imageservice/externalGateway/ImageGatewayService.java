package com.db.assignment.imageservice.externalGateway;

import com.db.assignment.imageservice.model.ExternalImageResponseDto;
import com.db.assignment.imageservice.model.ImageDto;

public interface ImageGatewayService {

    ExternalImageResponseDto fetchImage(ImageDto imageDto);
}
