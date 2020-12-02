package com.serverless

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.serverless.infra.FileHandler
import com.serverless.infra.JGitHandler
import com.serverless.infra.KinesisHandler
import org.apache.http.HttpStatus
import org.apache.logging.log4j.LogManager
import org.eclipse.jgit.api.errors.GitAPIException
import java.io.File
import java.io.IOException
import java.util.*

class Handler : RequestHandler<Map<String, Any>, ApiGatewayResponse> {

    companion object {
        private val LOG = LogManager.getLogger(Handler::class.java)
        private const val REPO_URL = "repo_url"
        private const val BRANCH = "branch"
        private const val USER = "user"
        private const val PASSWORD = "password"
    }

    private val fileHandler = FileHandler();
    private val jgitHandler = JGitHandler();
    private val kinesisHandler = KinesisHandler();

    override fun handleRequest(input: Map<String, Any>, context: Context): ApiGatewayResponse {
        LOG.info("received: {}", input)
        return try {
            val repoUrl = input[REPO_URL] as String? ?: error("repo_url should be mandatory")
            val branch = input[BRANCH] as String? ?: error("branch should be mandatory")
            val user = input[USER] as String? ?: error("user should be mandatory")
            val password = input[PASSWORD] as String? ?: error("password should be mandatory")

            validateRepoUrl(repoUrl)
            LOG.info("validation done for repo_url: {}", repoUrl)

            //clone repo to tmp -> stream to kinesis -> clean up tmp
            val tmpDirectory: File = fileHandler.prepareLocalDirectory(repoUrl)
            val localRepoPath: String = jgitHandler.cloneRepoToLocal(repoUrl, branch, user, password, tmpDirectory)
            val result = kinesisHandler.streamFile(localRepoPath, repoUrl, branch)
            fileHandler.cleanTmpDirectory(tmpDirectory)

            val responseBody = Response("Project is cloned and upload to Kinesis", result)
            return ApiGatewayResponse.build {
                statusCode = 200
                objectBody = responseBody
                headers = Collections.singletonMap<String, String>("X-Powered-By", "AWS Lambda & serverless")
            }
        } catch (e: GitAPIException) {
            returnErrorResponse(input, "Fail to clone git repo: ${e.message}")
        } catch (e: AmazonServiceException) {
            returnErrorResponse(input, "Fail to stream Kinesis: ${e.errorMessage}")
        } catch (e: IOException) {
            returnErrorResponse(input, "$e.message")
        } catch (e: IllegalStateException) {
            returnErrorResponse(input, "IllegalStateException: ${e.message}")
        } catch (e: Exception) {
            returnErrorResponse(input, "Exception: ${e.message}")
        }
    }

    private fun validateRepoUrl(repoUrl: String) {
        if (!repoUrl.endsWith(".git")) {
            throw RuntimeException("invalid repo_url")
        }
    }

    private fun returnErrorResponse(input: Map<String, Any>, errMsg: String): ApiGatewayResponse {
        LOG.error(errMsg)
        return ApiGatewayResponse.build {
            statusCode = HttpStatus.SC_EXPECTATION_FAILED
            objectBody = Response(errMsg, input)
            headers = Collections.singletonMap<String, String>("X-Powered-By", "AWS Lambda & serverless")
        }
    }
}