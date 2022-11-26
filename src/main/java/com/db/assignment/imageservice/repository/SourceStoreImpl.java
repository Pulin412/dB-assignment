package com.db.assignment.imageservice.repository;

import com.db.assignment.imageservice.model.ImageRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class SourceStoreImpl implements SourceStoreInterface{

    @Value("${mock.response.getOriginalImageFromSource}")
    private String mock_response_getOriginalImageFromSource;

    @Override
    public String getOriginalImageFromSource(ImageRequestDto imageRequestDto) {
        // Call to the external system to fetch from source [mocked_external_source]
        return mock_response_getOriginalImageFromSource;
    }
}
