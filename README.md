# resource-service
## Implements REST API CRUD service for files. Used for data storing. Service uses  cloud-based storage emulation (S3 bucket with localstack - running aws locally). Service tracks resources (with resource location) in the postgresql database

NOTE! 
Add `config` and `credentials` file to local `~/.aws` folder (requires `aws-cli/awslocal' to be installed)
  
  - config:
    ```properties
    [default]
    region = us-east-1
    ```
  - credentials:
    ```properties
    [default]
    aws_access_key_id = test
    aws_secret_access_key = test
    ```

- To enable simulated s3 bucket storage for files. Run docker-compose file from root docker-compose folder.
- (Optional) Once docker containers are running for localstack and postgres, create s3 bucket for resource service and file storage for resource service to use
```bash
awslocal s3 mb s3://resource-service-bucket
```
- LATEST UPDATE: If skiped, bucket will be created automatically on app startup, if it doesn't exist


Check if files are added/removed to/from the storage after triggering the REST endpoints through terminal
```bash
awslocal s3 ls resource-service-bucket --recursive --human-readable --summarize
```
- Added Kafka messaging producer configuration (`src/main/../messaging/KafkaProducerConfiguration.java`) 
- Added test coverage which will be compelted for further modules.
