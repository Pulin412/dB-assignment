package com.db.assignment.image_service.service;

import com.db.assignment.image_service.exception.GenericException;
import com.db.assignment.image_service.exception.ImageNotFoundException;
import com.db.assignment.image_service.model.ExternalImageResponseDto;
import com.db.assignment.image_service.model.ImageMetaData;
import com.db.assignment.image_service.model.ImageRequestDto;
import com.db.assignment.image_service.model.enums.ImageExtensionEnum;
import com.db.assignment.image_service.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.image_service.model.enums.ScaleTypeEnum;
import com.db.assignment.image_service.model.image_type.ImageConfigContainer;
import com.db.assignment.image_service.model.image_type.ImageType;
import com.db.assignment.image_service.model.image_type.ThumbnailImageType;
import com.db.assignment.image_service.service.image_type_strategy.ImageTypeStrategy;
import com.db.assignment.image_service.service.image_type_strategy.ImageTypeStrategyFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class ImageServiceUnitTests {

    @Mock
    private ImageGatewayService imageGatewayService;
    @Mock
    private LocalStoreService localStoreService;
    @Mock
    private ImageTypeStrategyFactory imageTypeStrategyFactory;

    @InjectMocks
    private ImageServiceImpl imageService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenReferenceNotGiven_thenThrowException(){
        when(imageTypeStrategyFactory.findStrategy(Mockito.any())).thenReturn(getThumbnailStrategy());
        Assertions.assertThrows(GenericException.class, ()-> imageService.getImage(getInvalidRequest()));
    }

    @Test
    public void whenInvalidImageTypeGiven_thenThrowException(){
        when(imageTypeStrategyFactory.findStrategy(Mockito.any())).thenReturn(getThumbnailStrategy());
        Assertions.assertThrows(GenericException.class, ()-> imageService.getImage(getInvalidRequest()));
    }

    @Test
    public void whenValidRequestAndOptimisedImagePresentInS3_thenReturnOptimisedImageUrl(){
        when(imageTypeStrategyFactory.findStrategy(Mockito.any())).thenReturn(getThumbnailStrategy());
        when(localStoreService.getOptimisedImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("/optimised/image"));
        Assertions.assertEquals("/optimised/image", imageService.getImage(getValidRequest()).getS3BucketUrl());
    }

    @Test
    public void whenOptimisedImageNotPresentButOriginalImagePresentInS3_thenOptimiseOriginalImageAndStoreInS3ThenReturn(){
        when(imageTypeStrategyFactory.findStrategy(Mockito.any())).thenReturn(getThumbnailStrategy());
        when(localStoreService.getOptimisedImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto(""));
        when(localStoreService.getOriginalImageURL(Mockito.any())).thenReturn("/url/to/fetch/from/s3");
        when(localStoreService.getOriginalImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("/original/from/s3"));
        when(localStoreService.optimise(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("/optimised/from/s3"));
        when(localStoreService.save(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("saved/optimised/from/s3"));
        Assertions.assertEquals("saved/optimised/from/s3", imageService.getImage(getValidRequest()).getS3BucketUrl());
    }

    @Test
    public void whenOriginalImageNotPresentInS3ButPresentInSource_thenDownloadAndOptimiseAndReturnOriginalImageUrlFromSource(){
        when(imageTypeStrategyFactory.findStrategy(Mockito.any())).thenReturn(getThumbnailStrategy());
        when(localStoreService.getOptimisedImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto(""));
        when(localStoreService.getOriginalImageURL(Mockito.any())).thenReturn("/url/to/fetch/from/s3");
        when(localStoreService.getOriginalImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto(""));
        when(imageGatewayService.getOriginalImageFromSource(Mockito.any())).thenReturn("/optimised/from/source");
        when(localStoreService.optimise(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("/optimised/from/source"));
        when(localStoreService.save(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("saved/optimised/from/source"));
        Assertions.assertEquals("saved/optimised/from/source", imageService.getImage(getValidRequest()).getS3BucketUrl());
    }

    @Test
    public void whenOriginalImageNotPresentInS3AndNotPresentInSource_thenThrowImageNotFoundException(){
        when(imageTypeStrategyFactory.findStrategy(Mockito.any())).thenReturn(getThumbnailStrategy());
        when(localStoreService.getOptimisedImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto(""));
        when(localStoreService.getOriginalImageURL(Mockito.any())).thenReturn("/url/to/fetch/from/s3");
        when(localStoreService.getOriginalImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto(""));
        when(imageGatewayService.getOriginalImageFromSource(Mockito.any())).thenReturn("");
        Assertions.assertThrows(ImageNotFoundException.class, ()-> imageService.getImage(getValidRequest()));
    }

    private ImageTypeStrategy getThumbnailStrategy() {
        return new ImageTypeStrategy() {
            @Override
            public ImageType getImageType() {
                return ThumbnailImageType.builder()
                        .imageConfigContainer(ImageConfigContainer.builder()
                                .height(10)
                                .width(20)
                                .quality(99)
                                .fillColor("0x45E213")
                                .scaleType(ScaleTypeEnum.CROP)
                                .imageExtension(ImageExtensionEnum.JPG)
                                .build())
                        .build();
            }

            @Override
            public ImageTypeStrategyNameEnum getImageTypeStrategyNameEnum() {
                return ImageTypeStrategyNameEnum.THUMBNAIL;
            }
        };
    }

    private ImageRequestDto getValidRequest(){
        return ImageRequestDto.builder()
                .preDefinedType("thumbnail")
                .seo("seo")
                .reference("/image.jpg")
                .imageMetaData(ImageMetaData.builder().imageId(UUID.randomUUID()).build())
                .build();
    }

    private ImageRequestDto getInvalidRequest(){
        return ImageRequestDto.builder()
                .preDefinedType("random")
                .seo("seo")
                .reference(null)
                .imageMetaData(ImageMetaData.builder().imageId(UUID.randomUUID()).build())
                .build();
    }

    private Optional<ExternalImageResponseDto> getOptionalExternalImageResponseDto(String response) {
        return Optional.of(ExternalImageResponseDto.builder()
                .imageId(UUID.randomUUID())
                .sourceImageUrl(response)
                .build());
    }

}
