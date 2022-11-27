package com.db.assignment.imageservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class ExternalImageResponseDto {

    private UUID imageId;
    private String sourceImageUrl;
}
