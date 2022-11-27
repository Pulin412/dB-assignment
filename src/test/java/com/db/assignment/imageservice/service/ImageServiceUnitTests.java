package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.exception.GenericException;
import com.db.assignment.imageservice.exception.ImageNotFoundException;
import com.db.assignment.imageservice.model.ExternalImageResponseDto;
import com.db.assignment.imageservice.model.ImageMetaData;
import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.enums.ImageExtensionEnum;
import com.db.assignment.imageservice.model.enums.ImageTypeStrategyNameEnum;
import com.db.assignment.imageservice.model.enums.ScaleTypeEnum;
import com.db.assignment.imageservice.model.imageType.ImageType;
import com.db.assignment.imageservice.model.imageType.Thumbnail_ImageType;
import com.db.assignment.imageservice.repository.SourceStoreRepo;
import com.db.assignment.imageservice.service.imageTypeStrategy.ImageTypeStrategy;
import com.db.assignment.imageservice.service.imageTypeStrategy.ImageTypeStrategyFactory;
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
        when(s3OperationService.getOptimisedImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("/optimised/image"));
        Assertions.assertEquals("/optimised/image", imageService.getImage(getValidRequest()).getS3BucketUrl());
    }

    @Test
    public void whenOptimisedImageNotPresentButOriginalImagePresentInS3_thenOptimiseOriginalImageAndStoreInS3ThenReturn(){
        when(imageTypeStrategyFactory.findStrategy(Mockito.any())).thenReturn(getThumbnailStrategy());
        when(s3OperationService.getOptimisedImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto(""));
        when(s3OperationService.getOriginalImageURL(Mockito.any())).thenReturn("/url/to/fetch/from/s3");
        when(s3OperationService.getOriginalImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("/original/from/s3"));
        when(s3OperationService.optimise(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("/optimised/from/s3"));
        when(s3OperationService.save(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("saved/optimised/from/s3"));
        Assertions.assertEquals("saved/optimised/from/s3", imageService.getImage(getValidRequest()).getS3BucketUrl());
    }

    @Test
    public void whenOriginalImageNotPresentInS3ButPresentInSource_thenDownloadAndOptimiseAndReturnOriginalImageUrlFromSource(){
        when(imageTypeStrategyFactory.findStrategy(Mockito.any())).thenReturn(getThumbnailStrategy());
        when(s3OperationService.getOptimisedImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto(""));
        when(s3OperationService.getOriginalImageURL(Mockito.any())).thenReturn("/url/to/fetch/from/s3");
        when(s3OperationService.getOriginalImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto(""));
        when(sourceStoreRepo.getOriginalImageFromSource(Mockito.any())).thenReturn("/optimised/from/source");
        when(s3OperationService.optimise(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("/optimised/from/source"));
        when(s3OperationService.save(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto("saved/optimised/from/source"));
        Assertions.assertEquals("saved/optimised/from/source", imageService.getImage(getValidRequest()).getS3BucketUrl());
    }

    @Test
    public void whenOriginalImageNotPresentInS3AndNotPresentInSource_thenThrowImageNotFoundException(){
        when(imageTypeStrategyFactory.findStrategy(Mockito.any())).thenReturn(getThumbnailStrategy());
        when(s3OperationService.getOptimisedImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto(""));
        when(s3OperationService.getOriginalImageURL(Mockito.any())).thenReturn("/url/to/fetch/from/s3");
        when(s3OperationService.getOriginalImageFromS3(Mockito.any())).thenReturn(getOptionalExternalImageResponseDto(""));
        when(sourceStoreRepo.getOriginalImageFromSource(Mockito.any())).thenReturn("");
        Assertions.assertThrows(ImageNotFoundException.class, ()-> imageService.getImage(getValidRequest()));
    }

    private ImageTypeStrategy getThumbnailStrategy() {
        return new ImageTypeStrategy() {
            @Override
            public ImageType getImageType() {
                return Thumbnail_ImageType.builder()
                        .height(10)
                        .width(20)
                        .quality(99)
                        .fillColor("0x45E213")
                        .scaleType(ScaleTypeEnum.CROP)
                        .imageExtension(ImageExtensionEnum.JPG)
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
