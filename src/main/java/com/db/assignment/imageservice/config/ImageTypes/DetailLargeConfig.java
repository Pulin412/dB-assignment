package com.db.assignment.imageservice.config.ImageTypes;

import com.db.assignment.imageservice.model.enums.ImageExtensionEnum;
import com.db.assignment.imageservice.model.enums.ScaleTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "image.type.detail-large")
@ConfigurationPropertiesScan
@Getter
@Setter
public class DetailLargeConfig {

    private int height;
    private int width;
    private int quality;
    private ScaleTypeEnum scaleType;
    private String fillColor;
    private ImageExtensionEnum imageExtension;
}
