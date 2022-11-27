package com.db.assignment.imageservice.service;

import com.db.assignment.imageservice.model.ImageRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class S3OperationServiceTests {

    private final S3OperationServiceImpl s3OperationService = new S3OperationServiceImpl();

    @Test
    public void whenImageTypeAndReferencePassed_thenReturnValidBucketDirectory(){
        Assertions.assertEquals("/thumbnail/0277/9010/0277901000150001_pro_mod_frt_02_1108_1528_1059540.jpg"
                , s3OperationService.getBucketPathFromFileName("0277901000150001/pro/mod/frt_02_1108_1528_1059540.jpg",new StringBuilder("/thumbnail/")).toString());
    }

    @Test
    public void whenValidRequest_thenValidS3UrlReturned(){
        Assertions.assertEquals("~/thumbnail/0277/9010/0277901000150001_pro_mod_frt_02_1108_1528_1059540.jpg", s3OperationService.createS3Url(ImageRequestDto.builder().preDefinedType("thumbnail").reference("0277901000150001/pro/mod/frt_02_1108_1528_1059540.jpg").build()));
    }
}
