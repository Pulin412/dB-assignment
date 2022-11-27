# dB-assignment

Image service that can serve optimized images based on predefined properties.

## Tech stack and Design patterns
- Minimum Java 17, Spring boot 3.0.0, H2 in-memory database.
- `Builder Pattern` to construct objects step by step throughout the API.
- `Factory and Strategy` design pattern for ImageTypes config to choose the Pre-defined image type and the related configurations on run-time. Good for `Open-Closed Principal` (SOLID principles).
- `Gateway pattern` for external systems interactions. Good for Separation of concerns while interacting with multiple external services for added operations like mapping the DTOs, making connections etc. More external systems can be added later on and associated with the Gateway instead of changing any configuration in the main Service. 
- `Data Transfer Objects (DTO)` to transfer data between different layers and encapsulate all the required details within an object.


## NFRs
- Image service needs to be highly available.
- Image service should be highly reliable.


## High Level System Design
- Incoming request from clients to application servers with load balancer in front.
- Application servers host the image service.
- Image server connects with Database servers, to store images metadata, with load balancer in front.
- Image server connects with S3 storage service in AWS to store images.
- NoSql to save images metadata. Data sharding with a partitioning scheme based on imageId.


## Low Level Design 

### Controller Layer

Incoming Request is received by the `ImageController.class`. There are 2 endpoints for fetching and flushing an image.

```
 Fetch - GET localhost:8080/image/show/thumbnail/bb?reference=/somedir/anotherdir/abcdef.jpg
 Flush - DELETE localhost:8080/image/flush/thumbnail?reference=0277901000150001_pro_mod_frt_02_1108_1528_1059540.jpg
```

### Service Layer

Multiple services are implemented to handle certain functionalities and to isolate the responsibilities among services.

- [ImageService](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/service/ImageService.java):

    - Main service responsible to fetch and flush the optimised images back to the controller layer.
    - Interacts with other services to connect to S3 or other external systems.
    - For future extensibility, the same service can be reused and other storage services implementations can be done.
    - This also includes the retry mechanism in case `save` operation throws exception which is captured as `CustomS3Exception` in the API. Following [properties](https://github.com/Pulin412/dB-assignment/blob/main/src/main/resources/retryConfig.properties) can be modified to customize retries -
   ```properties
    retry.maxAttempts=1
    retry.maxDelay=200 
  ```
  - To test the retry functionality, comment the mocked response with the thrown exception [here](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/repository/S3StoreRepoImpl.java#L66)
  ```java
        //  throw new CustomS3Exception("error");
            return Optional.of(ExternalImageResponseDto.builder()
                .imageId(UUID.randomUUID())
                .sourceImageUrl(mock_response_save)
                .build());
  ```   

- [LocalStoreService](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/service/LocalStoreService.java)

  - This is the service that acts as an interface between local storages connecting to the API.
  - In the current implementation, it connects with the S3OperationService to handle the S3 related operations.

- [S3OperationService](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/service/S3OperationService.java)

    - This service is responsible for operations related with S3 AWS service.
    - This service can be used to add more operations like listing objects from buckets, listing buckets etc.

- [ImageGatewayService](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/service/ImageGatewayService.java)

    - This is the Gateway service used to connect to external services like `source` system.
    - This service can expose methods to map the response from the external services to internal services usable object with minimum required attributes.
    - This service can also handle other functionalities related to connection with the external systems for eg. Security, Authorization while connecting.

- [ImageTypeStrategy](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/service/imageTypeStrategy/ImageTypeStrategy.java)

    - This is the strategy interface used within the application to get the appropriate Pre-defined Image Type and related configuration.
  ```java
    ImageTypeStrategy imageTypeStrategy = imageTypeStrategyFactory.findStrategy(ImageTypeStrategyNameEnum.valueOf(imageType.toUpperCase()));
    return imageTypeStrategy.getImageType();
    ```
    - Above code block returns the required pre configured Pre-defined Image type object on runtime using the Strategy pattern.
    - Configurations are fetched from the `application.properties`. Sample properties - 
  ```properties
    image.type.thumbnail.height=10
    image.type.thumbnail.width=20
    image.type.thumbnail.quality=90
    image.type.thumbnail.scaleType=crop
    image.type.thumbnail.fillColor=0x45E213
    image.type.thumbnail.imageExtension=jpg
    ```
    - If a new addition is made, it can be done gracefully by adding new strategy and properties, without modifying any existing service/properties.

### Repository Layer

Dummy repositories are present in the code to show the place to put the connection with Databases/services in future.

- [S3StoreRepo](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/repository/S3StoreRepo.java)

    - This repository is responsible to connect to S3 service and perform CRUD and other operations related to objects (images in this case).

- [SourceStoreRepo](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/repository/SourceStoreRepo.java)

    - This repository is responsible to connect to the Gateway `ImageGatewayService` and get the response.
    - This is additional layer before the Gateway, in case other operations/implementations are required before the response is passed back to the service.


### Configurations

- [AwsS3Config](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/config/AwsS3Config.java) 

    - This configuration bean is required when a connection is required with AWS S3 service.
    - All the connection related details can be added here and can be used by the `S2StoreRepo` to do operations.

- [RetryConfig](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/config/RetryConfig.java)

    - This configuration bean is used to implement the Retry mechanism on failure to store object in S3 and if `CustomS3Exception` is received.
    - This uses the properties from `retryConfig.properties` to control the number of retries and gap between the retries.

- [DefaultListenerSupport](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/config/DefaultListenerSupport.java)

    - This provides the callbacks upon retries and is used to log the details while retrying the operation. 

- [ImageTypes](https://github.com/Pulin412/dB-assignment/tree/main/src/main/java/com/db/assignment/imageservice/config/ImageTypes)

    - These configurations are available with the pre-configured properties for each Pre-defined Image type from application properties.


### Exception Handling

A unified exception handling is implemented for the whole application using `RestControllerAdvice`

- [GlobalExceptionController](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/exception/GlobalExceptionController.java)

    - This class handles all the custom exceptions thrown by the application.
    - If any custom Exception is added in future, can be added here for the entire application.

- A few custom Exceptions are implemented in the GlobalExceptionController - `ImageNotFoundException`, `CustomS3Exception`, `GenericException`
- More can be added with proper business justifications and error response codes.


### Models and DTOs

- [ImageRequestDTO](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/model/ImageRequestDto.java) and [ImageResponseDTO](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/model/ImageResponseDto.java) are the main DTOs to handle the data transfer between Controller and ImageService.
- [ImageMetaData](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/model/ImageMetaData.java) is present for future use. Image metadata can be saved in a NoSql Database for quick access.
- [ExternalImageDto](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/model/ExternalImageDto.java) and [ExternalImageResponseDto](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/model/ExternalImageResponseDto.java) are used to communicate between ImageService and S3OperationService and SourceStoreRepo. We can separate the DTOs for these two services but since its a dummy call, both calls use the same DTO.
- ImageType

    - [ImageType](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/model/imageType/ImageType.java) is used as an Abstract class with properties common to all ImageType.
    - Implementation for the ImageType Abstract class are added such as [Thumbnail_ImageType](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/model/imageType/Thumbnail_ImageType.java).

### Unit test cases

- A few [Junits](https://github.com/Pulin412/dB-assignment/tree/main/src/test/java/com/db/assignment/imageservice/service) are written for ImageService and S3OperationService methods using `Mockito`.

### Logs Appender

- A dummy [custom](https://github.com/Pulin412/dB-assignment/blob/main/src/main/java/com/db/assignment/imageservice/config/CustomDbAppender.java) DB log appender is implemented as the support for logback DB appender has been removed from log4j due to vulnerabilities [read here](https://logback.qos.ch/news.html#logback.db.1.2.11.1)

## Steps to run the API

- Use the Spring boot maven plugin to run the application - 

```shell
mvn spring-boot:run
```

Access the below-mentioned sample URLs to hit the endpoints - 

Fetch Image - 
```html
    curl -X GET 'http://localhost:8080/image/show/detail_large/bb?reference=%2Fsomedir%2Fanotherdir%2Fabcdef.jpg'
```

Flush Image -
```html
    curl -X DELETE 'http://localhost:8080/image/flush/original?reference=%2F027%2F790%2F13_0277901000150001_pro_mod_frt_02_1108_1528_1059540.jpg'
```

> All mocked responses for the repositories can be handled via application.properties using these values:
  ```properties
      # Mocked external system URLs and responses
      com.dbf.s3-service=https://mocked-s3-service.dbf.net/
      com.dbf.ext-source-service=https://mocked-ext-source-service.dbf.net/
      mock.response.getOriginalImageFromSource=~/dir1/dir2/sourceImage
      
      # Mocked responses for fetchImage method
      mock.response.getOptimisedImageFromS3=
      mock.response.getOriginalImageFromS3=
      mock.response.optimise=/optimised/image/sample.png
      mock.response.save=/optimised/saved/in/s3/image/sample.png
      
      # Mocked responses for flush method
      mock.response.doesObjectExist=true
      mock.response.flushImage=true
  ```


## Improvements
- DynamoDB (key value database)/ Cassandra DB (Distributed NoSql) to store images metadata.
- Key generation service to generate image IDs for sharded database.
- Introduce a _Least Recently Used_ based Memcached cache with 80-20% rule i.e. 20% of daily read volume is generating 80% of the incoming traffic.
- Isolate reads with writes. Implement queue based solution for writes if Consistency can take a hit instead of Availability.