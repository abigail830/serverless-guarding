package com.serverless.model;

import java.util.Map;

public class ClonePrjResponse extends ProjectInfo {

    S3Info s3Info;

    public ClonePrjResponse(Map<String, Object> input) {
        super(input);
    }
    public ClonePrjResponse(Map<String, Object> input, S3Info s3Info) {
        super(input);
        this.s3Info = s3Info;
    }

    @Override
    public String toString() {
        return "ClonePrjResponse{" +
                "repoUrl='" + getRepoUrl() + '\'' +
                ", userName='" + getUserName() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", branch='" + getBranch() + '\'' +
                ", s3Info=" + s3Info +
                '}';
    }
}
