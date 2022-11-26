package com.db.assignment.imageservice.externalGateway;

import com.db.assignment.imageservice.model.ExternalImageResponseDto;
import com.db.assignment.imageservice.model.ExternalImageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageGatewayServiceImpl implements ImageGatewayService{

    @Value("${mock.response.getOriginalImageFromSource}")
    private String mock_response_getOriginalImageFromSource;

    @Override
    public ExternalImageResponseDto fetchImage(ExternalImageDto externalImageDto) {
        return ExternalImageResponseDto.builder().mockedResponse(mock_response_getOriginalImageFromSource).build();
    }
}
