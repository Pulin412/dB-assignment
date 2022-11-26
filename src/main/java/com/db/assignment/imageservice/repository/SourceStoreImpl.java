package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.externalGateway.ImageGatewayService;
import com.db.assignment.imageservice.model.ImageDto;
import org.springframework.stereotype.Repository;

@Repository
public class SourceStoreImpl implements SourceStoreInterface{

    private final ImageGatewayService imageGatewayService;

    public SourceStoreImpl(ImageGatewayService imageGatewayService) {
        this.imageGatewayService = imageGatewayService;
    }

    @Override
    public String getOriginalImageFromSource(ImageDto imageDto) {
        // Call to the external system to fetch from source [mocked_external_source]
        return imageGatewayService.fetchImage(imageDto).getMockedResponse();
    }
}
