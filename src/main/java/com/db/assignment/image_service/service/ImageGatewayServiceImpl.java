package com.db.assignment.image_service.service;

import com.db.assignment.image_service.model.ExternalImageResponseDto;
import com.db.assignment.image_service.model.ExternalImageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageGatewayServiceImpl implements ImageGatewayService{

    @Value("${mock.response.getOriginalImageFromSource}")
    private String mock_response_getOriginalImageFromSource;

    @Override
    public String getOriginalImageFromSource(ExternalImageDto externalImageDto) {
        return ExternalImageResponseDto.builder().sourceImageUrl(mock_response_getOriginalImageFromSource).build().getSourceImageUrl();
    }
}
