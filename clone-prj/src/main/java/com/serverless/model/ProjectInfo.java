package com.serverless.model;

import com.serverless.Handler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class ProjectInfo {

    private static final Logger LOG = LogManager.getLogger(Handler.class);

    private String repoUrl;
    private String userName;
    private String password;
    private String branch;

    public ProjectInfo(Map<String, Object> input) {
        this.repoUrl = (String)input.get("repoUrl");
        this.userName = (String)input.get("userName");
        this.password = (String)input.get("password");
        this.branch = (String)input.get("branch");
    }

    public Boolean isValid() {
        if (repoUrl == null) {
            LOG.error("repoUrl should be provided");
            return false;
        }
        if (!repoUrl.endsWith(".git")) {
            LOG.error("invalid repo_url");
            return false;
        }
        return true;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    @Override
    public String toString() {
        return "ProjectInfo{" +
                "repoUrl='" + repoUrl + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", branch='" + branch + '\'' +
                '}';
    }
}
