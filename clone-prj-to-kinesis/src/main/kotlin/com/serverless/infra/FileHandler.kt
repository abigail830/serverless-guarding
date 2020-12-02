package com.serverless.infra

import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager
import java.io.File
import java.io.IOException

class FileHandler {
    private val LOG = LogManager.getLogger(FileHandler::class.java)

    fun getProjectName(repoUrl: String): String {
        return repoUrl.substring(repoUrl.lastIndexOf("/") + 1, repoUrl.lastIndexOf("."))
    }

    fun prepareLocalDirectory(repoUrl: String): File {
        val prjName = getProjectName(repoUrl)
        val tmpDirectory = File("/tmp/" + prjName + "/" + System.currentTimeMillis())
        if (!tmpDirectory.exists()) {
            val result = tmpDirectory.mkdirs()
            LOG.info("create directory {} with result {}", tmpDirectory, result)
        }
        return tmpDirectory
    }

    @Throws(IOException::class)
    fun cleanTmpDirectory(tmpDirectory: File?) {
        FileUtils.deleteDirectory(tmpDirectory)
    }
}