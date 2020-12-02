package com.serverless.infra;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.util.Arrays;

public class JGitHelper {

    private static final Logger LOG = LogManager.getLogger(JGitHelper.class);

    public String cloneRepoToLocal(String repoUrl, String branch, String user, String password,
                                    File tmpDirectory) throws GitAPIException {
        LOG.info("Going to clone the repo[{}] to local dir {}", repoUrl, tmpDirectory);
        Git.cloneRepository()
                .setURI(repoUrl)
                .setDirectory(tmpDirectory)
                .setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(user, password))
                .setBranchesToClone(Arrays.asList("refs/heads/" + branch))
                .setBranch("refs/heads/" + branch)
                .call();
        return tmpDirectory.getAbsolutePath();
    }
}
