# resource-service

## Implements REST API CRUD service for files. Used for data storing. Service uses  cloud-based storage emulation (S3 bucket with localstack - running aws locally). Service tracks resources (with resource location) in the postgresql database. Runs as a docker container and with docker-compose runs additional services in the same network.

**NOTE!** - *compose this service first because of the additional required services that are deployed and used by others!!*

### Startup

 **Prerequisites**:
 
  - `aws-cli/awslocal' to be installed
  - Docker engine installed
  - `docker-compose` app installed
  - OPTIONAL: local `maven` installed, embeded wrapped can be used instead
   
1. First, Add `config` and `credentials` file to local `~/.aws` folder (requires)
 
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

2. Create common docker network for all services, if not previously  -`resource-network`
    ```bash
     docker network create resource-network
    ```
    
3. OPTIONAL: manually enable simulated s3 bucket storage for files.
    ```bash
    awslocal s3 mb s3://resource-service-bucket
    ```
    If skiped, bucket will be created automatically on app startup, if it doesn't exist
    
4. Since it is a maven project, first build it using your local installed maven or using maven wrapper inside the project
   ```bash
    mvn clean package
   ```
    or using maven wrapper
    ```bash
    mvnw clean package
    ```
  
5. Inside project root, build local docker image of the project(project already contains prepared `Dockerfile`
   ```bash
   docker build -t resource-service-local-image .
   ```
     
6. After checking that the image is properly built and ready, start `resource-service` and it's corresponding docker containers <br>
   (check `docker-compose` file from the project):
   - [service-configuration-server](https://github.com/Branjash/services-configuration-server) - ! **important for all other services** !!
   - Localstack - aws cloud local simulation
   - Postgres 
   - Zookeper
   - Kafka
   ```bash
   docker-compose up -d
   ```
    
  4. Finally you can chech if the containers are running and check logs to if they are running properly
     - check are they running
       ```bash
       docker ps
       ```
     - check are they running properly by checking logs (**container_id** - value listed after running previous command)
       ```bash
       docker logs -f *container_id* 

Check if files are added/removed to/from the storage after triggering the REST endpoints through terminal
```bash
awslocal s3 ls resource-service-bucket --recursive --human-readable --summarize
```
- Added Kafka messaging producer configuration (`src/main/../messaging/KafkaProducerConfiguration.java`) 
- Added test coverage which will be compelted for further modules.
