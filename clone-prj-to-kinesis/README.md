# Clone-Project-to-Kinesis

This is for clone the git project and stream to kinesis. 

This is the preparation step for further code analysis

#### Command for serverless framework
```
sls deploy
serverless invoke -f clone -d '{"repo_url": "https://github.com/abigail830/order-service.git", "branch": "master", "user": "abigail830", "password": "505Abigail616" }' -log

```

#### Input Map
```
{
    "repo_url": "https://github.com/spring-projects/spring-framework.git", 
    "branch": "master", 
    "user": <git-user-name>, 
    "password": <git-password> 
}

```
#### Response with S3 Bucket name
```
{
    "stream_name": "raw-repo", 
    "stream_size": <file-count-in-tmp-dir>
}

```
