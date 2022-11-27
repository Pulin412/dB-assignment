package com.db.assignment.image_service.config.Image_types;

import com.db.assignment.image_service.model.image_type.ImageConfigContainer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "image.type.detail-large")
@ConfigurationPropertiesScan
@Getter
@Setter
public class DetailLargeConfig {

    private ImageConfigContainer imageConfigContainer;
}
