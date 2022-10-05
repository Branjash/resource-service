# resource-service
## Implements CRUD service for files. Used for data storing. Service uses  cloud-based storage emulation (S3 bucket with localstack - running aws locally). Service tracks resources (with resource location) in the postgresql database

- To enable simulated s3 bucket storage for files. Run docker-compose file from root docker-compose folder.
- Once docker containers are running for localstack and postgres, create s3 bucket for resource service and file storage for resource service to use

```bash
awslocal s3 mb s3://resource-service-bucket
```

Check if files are added/removed to/from the storage after triggering the REST endpoints
```bash
awslocal s3 ls resource-service-bucket --recursive --human-readable --summarize
```
