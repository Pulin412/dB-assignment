package com.db.assignment.imageservice.config.ImageTypes;

import com.db.assignment.imageservice.model.imageType.ImageConfigContainer;
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
