package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.service.ImageGatewayService;
import com.db.assignment.imageservice.model.ExternalImageDto;
import org.springframework.stereotype.Repository;

@Repository
public class SourceStoreRepoImpl implements SourceStoreRepo {

    private final ImageGatewayService imageGatewayService;

    public SourceStoreRepoImpl(ImageGatewayService imageGatewayService) {
        this.imageGatewayService = imageGatewayService;
    }

    @Override
    public String getOriginalImageFromSource(ExternalImageDto externalImageDto) {
        // Call to the external system to fetch from source [mocked_external_source]
        return imageGatewayService.fetchImage(externalImageDto).getMockedResponse();
    }
}
