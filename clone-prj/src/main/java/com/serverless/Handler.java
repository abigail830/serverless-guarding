package com.serverless;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.serverless.infra.FileHelper;
import com.serverless.infra.JGitHelper;
import com.serverless.infra.S3Helper;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private static final Logger LOG = LogManager.getLogger(Handler.class);
    public static final String REPO_URL = "repo_url";
    public static final String BRANCH = "branch";
    public static final String USER = "user";
    public static final String PASSWORD = "password";

    private FileHelper fileHelper = new FileHelper();
    private S3Helper s3Helper = new S3Helper();
    private JGitHelper jGitHelper = new JGitHelper();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LOG.info("received: {}", input);
        try {
            validateMandatoryFields(input);

            String repoUrl = (String) input.get(REPO_URL);
            String branch = (String) input.get(BRANCH);
            String user = (String) input.get(USER);
            String password = (String) input.get(PASSWORD);
            LOG.info("validation done for repo_url: {}", repoUrl);

            //clone repo to tmp -> upload to S3 -> clean up tmp
            final File tmpDirectory = fileHelper.prepareLocalDirectory(repoUrl);
            String localRepoPath = jGitHelper.cloneRepoToLocal(repoUrl, branch, user, password, tmpDirectory);
            Map<String, Object> s3Info = s3Helper.uploadDirToS3(localRepoPath, fileHelper.getProjectName(repoUrl));

            fileHelper.cleanTmpDirectory(tmpDirectory);

            Response responseBody = new Response("Project is cloned and upload to S3", s3Info);
            return ApiGatewayResponse.builder()
                    .setStatusCode(200)
                    .setObjectBody(responseBody)
                    .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                    .build();

        } catch (GitAPIException e) {
            return returnErrorResponse(input, "Fail to clone git repo: " + e.getMessage());
        } catch (AmazonServiceException e) {
            return returnErrorResponse("Fail to upload S3: " + e.getErrorMessage());
        } catch (IOException e) {
            return returnErrorResponse("" + e.getMessage());
        } catch (Exception e) {
            return returnErrorResponse(input, e.getMessage());
        }
    }

    private ApiGatewayResponse returnErrorResponse(String errMsg) {
        LOG.error(errMsg);
        return ApiGatewayResponse.builder()
                .setStatusCode(HttpStatus.SC_EXPECTATION_FAILED)
                .setObjectBody(new Response(errMsg))
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                .build();
    }

    private ApiGatewayResponse returnErrorResponse(Map<String, Object> input, String errMsg) {
        LOG.error(errMsg);
        return ApiGatewayResponse.builder()
                .setStatusCode(HttpStatus.SC_EXPECTATION_FAILED)
                .setObjectBody(new Response(errMsg, input))
                .setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
                .build();
    }

    private void validateMandatoryFields(Map<String, Object> input) {
        if (input.get(REPO_URL) == null) {
            throw new RuntimeException("repo_url should be provided");
        }
        if (input.get(BRANCH) == null) {
            throw new RuntimeException("branch should be provided");
        }
        if (input.get(USER) == null) {
            throw new RuntimeException("user should be provided");
        }
        if (input.get(PASSWORD) == null) {
            throw new RuntimeException("password should be provided");
        }
        if (!input.get(REPO_URL).toString().endsWith(".git")) {
            throw new RuntimeException("invalid repo_url");
        }
    }
}
