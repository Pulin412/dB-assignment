package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.config.ImageTypes.DetailLargeConfig;
import com.db.assignment.imageservice.config.ImageTypes.PortraitConfig;
import com.db.assignment.imageservice.config.ImageTypes.ThumbnailConfig;
import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.imageType.ImageType;

public interface ImageTypeResolver {

    ImageType createImageType(ImageRequestDto imageRequestDto, String imageType, ThumbnailConfig thumbnailConfig, DetailLargeConfig detailLargeConfig, PortraitConfig portraitConfig);
}
