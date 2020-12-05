package com.serverless.model;

public class S3Info {

    private String bucketName;
    private String projectName;
    private String s3Folder;

    public S3Info() {
    }

    public S3Info(String bucketName, String projectName, String s3Folder) {
        this.bucketName = bucketName;
        this.projectName = projectName;
        this.s3Folder = s3Folder;
    }

    @Override
    public String toString() {
        return "S3Info{" +
                "bucketName='" + bucketName + '\'' +
                ", projectName='" + projectName + '\'' +
                ", s3Folder='" + s3Folder + '\'' +
                '}';
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getS3Folder() {
        return s3Folder;
    }

    public void setS3Folder(String s3Folder) {
        this.s3Folder = s3Folder;
    }
}
