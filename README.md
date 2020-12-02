

AWS Serverless
===========================================

Develop with Lambda
----------------------

至少两种方式开发：

* 纯手写: 可以参考 https://github.com/awsdocs/aws-lambda-developer-guide/tree/master/sample-apps/s3-java
* 用框架： Serverless Framework (可以少写CloudFormation)

### Serverless Framework

* Install Serverless framework: ``npm install -g serverless``

* Setup AWS IAM: Add user serveless-admin &  policy AdministratorAccess & Download csv and keey it well, we would see .aws folder is created with credentials after below command

  ```bash
  export AWS_ACCESS_KEY_ID=<key-id>
  export c=<access-key>
  export AWS_PROFILE="serverless-admin"
  serverless config credentials --provider aws --key $AWS_ACCESS_KEY_ID --secret $AWS_ACCESS_KEY_ID --profile $AWS_PROFILE
  ```

* Create project with template

  ```bash
  serverless create --help
  serverless create --template aws-java-maven --path clone-prj
  ```

* Update pom.xml

  ```
  <artifactId>clone-prj</artifactId>
  <name>clone-prj</name>
  ```

  

* Update serverless.yml (seems artifact name would auto added stage name at the end) 

  ```yml
   package:
    artifact: target/clone-prj-dev.jar
  ```

* Deploy: ``serverless deploy``

* Invoke

  ```bash
  qian.ping@qianpings-MacBook-Pro clone-prj % serverless invoke -f clone -l
  {
      "statusCode": 200,
      "body": "{\"message\":\"Go Serverless v1.x! Your function executed successfully!\",\"input\":{}}",
      "headers": {
          "X-Powered-By": "AWS Lambda & serverless"
      },
      "isBase64Encoded": false
  }
  --------------------------------------------------------------------
  START RequestId: ef827025-3bc9-4851-b502-99f3c7c6c942 Version: $LATEST
  2020-11-21 15:23:02 ef827025-3bc9-4851-b502-99f3c7c6c942 INFO  Handler:18 - received: {}
  END RequestId: ef827025-3bc9-4851-b502-99f3c7c6c942
  REPORT RequestId: ef827025-3bc9-4851-b502-99f3c7c6c942	Duration: 970.45 ms	Billed Duration: 1000 ms	Memory Size: 1024 MB	Max Memory Used: 124 MB	Init Duration: 1266.32 ms
  ```

  ```
  $ aws lambda invoke --function-name repoStream --payload '{"repo_url": "https://github.com/abigail830/order-service.git", "branch": "master", "user": "abigail830", "password": “505Abigail616” } response.json
  {
      "ExecutedVersion": "$LATEST",
      "StatusCode": 200
  }
  
  aws lambda invoke --function-name repoStream --payload '{"repo_url": "https://github.com/abigail830/order-service.git", "branch": "master", "user": "abigail830", "password": "505Abigail616" }' response.json
  ```
  
  


First lambda to clone github project and upload to S3
--------------------------------------------------------

* Setup credential (done above): https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html

* Reference: https://github.com/awsdocs/aws-lambda-developer-guide/tree/master/sample-apps/s3-java

* Create a S3 bucket named 'clone-prj' and update the policy to ensure have GET/PUT

````json
  {
    "Id": "Policy1605970945090",
    "Version": "2012-10-17",
    "Statement": [
     {
       "Sid": "Stmt1605970311420",
       "Action": [
         "s3:GetObject",
         "s3:PutObject"
       ],
       "Effect": "Allow",
       "Resource": "arn:aws:s3:::clone-prj/*",
       "Principal": "*"
     }
   ]
  }
````

* serverless invoke

```
qian.ping@qianpings-MacBook-Pro clone-prj % serverless invoke -f clone -l
{
  "statusCode": 200,
  "body": "{\"message\":\"Go Serverless v1.x! Your function executed successfully!\",\"input\":{}}",
  "headers": {
​    "X-Powered-By": "AWS Lambda & serverless"
  },
  "isBase64Encoded": false
}

\--------------------------------------------------------------------
START RequestId: ef827025-3bc9-4851-b502-99f3c7c6c942 Version: $LATEST
2020-11-21 15:23:02 ef827025-3bc9-4851-b502-99f3c7c6c942 INFO Handler:18 - received: {}
END RequestId: ef827025-3bc9-4851-b502-99f3c7c6c942
REPORT RequestId: ef827025-3bc9-4851-b502-99f3c7c6c942	Duration: 970.45 ms	Billed Duration: 1000 ms	Memory Size: 1024 MB	Max Memory Used: 124 MB	Init Duration: 1266.32 ms
```



## Kinesis

* 怎么决定shard

```
number_of_shards = max(incoming_write_bandwidth_in_KB/1000, outgoing_read_bandwidth_in_KB/2000)

incoming_write_bandwidth_in_KB =  average_data_size_in_KB multiplied by the number_of_transactions_per_seconds.

outgoing_read_bandwidth_in_KB = incoming_write_bandwidth_in_KB multiplied by the number_of_consumers.
```



