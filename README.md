# dB-assignment

Image service that can serve optimized images based on predefined properties.

## NFRs
- Image service needs to be highly available.
- Image service should be highly reliable.

## High Level System Design
- Incoming request from clients to application servers with load balancer in front.
- Application servers host the image service.
- Image server connects with Database servers, to store images metadata, with load balancer in front.
- Image server connects with S3 storage service in AWS to store images.
- NoSql to save images metadata. Data sharding with a partitioning scheme based on imageId.

### Improvements
- DynameDB (key value database)/ Cassandra DB (Distributed NoSql) to store images metadata.
- Key generation service to generate image IDs for sharded database.
- Introduce a _Least Recently Used_ based Memcached cache with 80-20% rule i.e. 20% of daily read volume is generating 80% of the incoming traffic.
- Isolate reads with writes.
- replace spring retry with queues.