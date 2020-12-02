package com.serverless.infra;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class FileHelper {
    private static final Logger LOG = LogManager.getLogger(FileHelper.class);

    @NotNull
    public String getProjectName(String repoUrl) {
        return repoUrl.substring(repoUrl.lastIndexOf("/") + 1, repoUrl.lastIndexOf("."));
    }

    @NotNull
    public File prepareLocalDirectory(String repoUrl) {
        String prjName = getProjectName(repoUrl);
        final File tmpDirectory = new File("/tmp/" + prjName + "/" + System.currentTimeMillis());
        if (!tmpDirectory.exists()) {
            Boolean result = tmpDirectory.mkdirs();
            LOG.info("create directory {} with result {}", tmpDirectory, result);
        }
        return tmpDirectory;
    }

    public void cleanTmpDirectory(File tmpDirectory) throws IOException {
        FileUtils.deleteDirectory(tmpDirectory);
    }

}
