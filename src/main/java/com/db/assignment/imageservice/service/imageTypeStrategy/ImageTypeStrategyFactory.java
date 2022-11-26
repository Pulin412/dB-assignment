package com.db.assignment.imageservice.service.imageTypeStrategy;

import com.db.assignment.imageservice.model.enums.ImageTypeStrategyNameEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ImageTypeStrategyFactory {

    private Map<ImageTypeStrategyNameEnum, ImageTypeStrategy> imageTypeStrategies;

    @Autowired
    public ImageTypeStrategyFactory(Set<ImageTypeStrategy> imageTypeStrategySet){
        createStrategy(imageTypeStrategySet);
    }

    private void createStrategy(Set<ImageTypeStrategy> imageTypeStrategySet) {
        imageTypeStrategies = new HashMap<>();
        imageTypeStrategySet.forEach(
                strategy -> imageTypeStrategies.put(strategy.getImageTypeStrategyNameEnum(), strategy));
    }

    public ImageTypeStrategy findStrategy(ImageTypeStrategyNameEnum imageTypeStrategyNameEnum) {
        return imageTypeStrategies.get(imageTypeStrategyNameEnum);
    }
}
