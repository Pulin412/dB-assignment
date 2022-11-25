package com.db.assignment.imageservice.controller;

import com.db.assignment.imageservice.model.ImageResponseDto;
import com.db.assignment.imageservice.service.ImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/image")
public class ImageController {

    private ImageService imageService;

    @GetMapping(value = "/show/{preDefinedType}/{seo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImageResponseDto> fetchImage(@PathVariable String preDefinedType, @PathVariable(required = false) String seo, @RequestParam("reference") String reference){
        return new ResponseEntity<>(imageService.getImage(preDefinedType, seo, reference), HttpStatus.OK);
    }

    @DeleteMapping(value = "/flush/{preDefinedType}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> flushImage(@PathVariable String preDefinedType, @RequestParam("reference") String reference){
        boolean isDeleted = imageService.flush(preDefinedType, reference);
        if(isDeleted)
            return new ResponseEntity<>("Image(s) deleted", HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
