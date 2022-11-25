package com.db.assignment.imageservice.controller;

import com.db.assignment.imageservice.model.ImageRequestDto;
import com.db.assignment.imageservice.model.ImageResponseDto;
import com.db.assignment.imageservice.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;
    private final Logger log = LoggerFactory.getLogger(ImageController.class);

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/show/{preDefinedType}/{seo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageResponseDto> fetchImage(@PathVariable String preDefinedType,
                                                       @PathVariable(required = false) String seo,
                                                       @RequestParam("reference") String reference) throws IOException {
        return new ResponseEntity<>(
                imageService.getImage(
                        ImageRequestDto.builder()
                                .preDefinedType(preDefinedType)
                                .seo(seo)
                                .reference(reference)
                                .build())
                , HttpStatus.OK);
    }

    @DeleteMapping(value = "/flush/{preDefinedType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> flushImage(@PathVariable String preDefinedType,
                                             @RequestParam("reference") String reference) {

        boolean isDeleted = imageService.flush(preDefinedType, reference);
        if (isDeleted) return new ResponseEntity<>("Image(s) deleted", HttpStatus.OK);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
