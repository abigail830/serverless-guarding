# Clone-Project

This is for clone the git project and download to S3. 

This is the preparation step for further code analysis

#### Command for serverless framework
```
sls deploy
serverless invoke -f clone -d '{"repo_url": "https://github.com/apache/dubbo.git", "branch": "master", "user": "abigail830", "password": "" }' -log

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
    "bucket_name": "clone-prj", 
    "key_prefix": <prjName/timestamp>
}

```
