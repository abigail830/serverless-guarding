package com.serverless;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.serverless.infra.FileHelper;
import com.serverless.infra.JGitHelper;
import com.serverless.infra.S3Helper;
import com.serverless.model.ClonePrjResponse;
import com.serverless.model.ProjectInfo;
import com.serverless.model.S3Info;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Handler implements RequestHandler<Map<String, Object>, String> {

    private static final Logger LOG = LogManager.getLogger(Handler.class);

    private FileHelper fileHelper = new FileHelper();
    private S3Helper s3Helper = new S3Helper();
    private JGitHelper jGitHelper = new JGitHelper();

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: {}", input);
//        String body = (String) input.get("body");
        try {
            ProjectInfo projectInfo = new ProjectInfo(input);
            if (projectInfo.isValid()) {
                LOG.info("validation done for repo_url: {}", projectInfo.getRepoUrl());

                // clone repo to tmp
                final File tmpDirectory = fileHelper.prepareLocalDirectory(projectInfo.getRepoUrl());
                String localRepoPath = jGitHelper.cloneRepoToLocal(projectInfo.getRepoUrl(),
                        projectInfo.getBranch(), projectInfo.getUserName(), projectInfo.getPassword(), tmpDirectory);
                // upload to S3
                S3Info s3Info = s3Helper.uploadDirToS3(localRepoPath,
                        fileHelper.getProjectName(projectInfo.getRepoUrl()));
                // clean up tmp
                fileHelper.cleanTmpDirectory(tmpDirectory);
                return new Gson().toJson(new ClonePrjResponse(input, s3Info));

            }else{
                return returnError(input, "Invalid input");
            }
        } catch (GitAPIException e) {
            return returnError(input, "Fail to clone git repo: " + e.getMessage());
        } catch (AmazonServiceException e) {
            return returnError(input, "Fail to upload S3: " + e.getErrorMessage());
        } catch (Exception e) {
            return returnError(input, e.getMessage());
        }
    }

    private String returnError(Map<String, Object> input, String msg) {
        Response responseBody = new Response(msg, input);
        return new Gson().toJson(responseBody);
    }
}
