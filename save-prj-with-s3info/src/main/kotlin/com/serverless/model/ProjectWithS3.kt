package com.serverless.model

data class ProjectWithS3(val repoUrl: String, val userName: String,
                         val password: String, val branch: String,
                         val s3Info: S3Info)

data class S3Info(val bucketName: String, val projectName: String, val s3Folder: String)