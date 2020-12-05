package com.serverless.infra;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.serverless.model.S3Info;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Map;

public class S3Helper {

    private static final Logger LOG = LogManager.getLogger(S3Helper.class);
    private static final String BUCKET_NAME = "clone-prj-to-s3";

    public S3Info uploadDirToS3(String sourcePath, String prjName) throws AmazonServiceException {
        long timestamp = System.currentTimeMillis();
        String key_prefix = prjName + "/" + timestamp;
        LOG.info("Going to upload {} to S3 bucket {}/{}", sourcePath, BUCKET_NAME, key_prefix);

        TransferManager xfer_mgr = TransferManagerBuilder.standard().build();

        MultipleFileUpload xfer = xfer_mgr.uploadDirectory(BUCKET_NAME,
                key_prefix, new File(sourcePath), true);
        // loop with Transfer.isDone()
        XferMgrProgress.showTransferProgress(xfer);
        // or block with Transfer.waitForCompletion()
        XferMgrProgress.waitForCompletion(xfer);

        xfer_mgr.shutdownNow();

        return new S3Info(BUCKET_NAME, prjName, key_prefix);
    }

    public void putFileToS3(Map<String, Object> s3Info, File completeIndicator) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
        String key_prefix = s3Info.get("projectName") + "/";
        try {
            PutObjectRequest pubObjectRequest = new PutObjectRequest(BUCKET_NAME, key_prefix, completeIndicator);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("title", "someTitle");
            pubObjectRequest.setMetadata(metadata);

            s3Client.putObject(pubObjectRequest);
            LOG.info("CompleteIndicator written to: " + BUCKET_NAME + "/" + key_prefix);
        } catch (AmazonServiceException e) {
            LOG.error("Fail to putFileToS3: {}", e.getErrorMessage());
        }
    }

}
