package com.serverless.infra

import org.apache.logging.log4j.LogManager
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import java.util.*

class JGitHandler {

    private val LOG = LogManager.getLogger(JGitHandler::class.java)

    @Throws(GitAPIException::class)
    fun cloneRepoToLocal(repoUrl: String, branch: String, user: String?, password: String?,
                         tmpDirectory: File): String {
        LOG.info("Going to clone the repo[{}] to local dir {}", repoUrl, tmpDirectory)
        Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(tmpDirectory)
                .setCredentialsProvider(
                        UsernamePasswordCredentialsProvider(user, password))
                .setBranchesToClone(Arrays.asList("refs/heads/$branch"))
                .setBranch("refs/heads/$branch")
                .call()
        return tmpDirectory.absolutePath
    }
}