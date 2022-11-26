package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.exception.GenericException;
import com.db.assignment.imageservice.model.ImageMetaData;
import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.repository.S3StoreRepo;
import com.db.assignment.imageservice.repository.SourceStoreRepo;
import com.db.assignment.imageservice.service.imageTypeStrategy.ImageTypeStrategyFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

public class ImageServiceUnitTests {

    @Mock
    private S3StoreRepo s3StoreRepo;
    @Mock
    private SourceStoreRepo sourceStoreRepo;
    @Mock
    private S3OperationService s3OperationService;
    @Mock
    private ImageTypeStrategyFactory imageTypeStrategyFactory;


    @InjectMocks
    private ImageServiceImpl imageService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /*

     */
    @Test
    public void whenReferenceNotGiven_thenThrowException(){
//        when(imageTypeStrategyFactory.findStrategy(Mockito.any())).thenReturn(getThumbnailStrategy());
        Assertions.assertThrows(GenericException.class, ()-> imageService.getImage(getRequest()));
    }

    private ImageRequestDto getRequest(){
        return ImageRequestDto.builder()
                .preDefinedType("thumbnail")
                .seo("seo")
                .reference(null)
                .imageMetaData(ImageMetaData.builder().imageId(UUID.randomUUID()).build())
                .build();
    }

}
