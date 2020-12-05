package com.serverless.api

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.gson.Gson
import com.serverless.ApiGatewayResponse
import com.serverless.Response
import com.serverless.domain.ProjectInfo
import com.serverless.infrastructure.ProjectInfoRepository
import org.apache.logging.log4j.LogManager
import org.jasypt.util.password.StrongPasswordEncryptor
import java.util.*

class CreateHandler : RequestHandler<Map<String,Any>, ApiGatewayResponse> {
    companion object {
        private val LOG = LogManager.getLogger(CreateHandler::class.java)
    }

    override fun handleRequest(input: Map<String,Any>, context: Context): ApiGatewayResponse {
        LOG.debug("Received ProjectInfo :  ${input["body"]}")
//        val body = input.get("body") as String

        val projectInfo = Gson().fromJson<ProjectInfo>(input.toString(), ProjectInfo::class.java)

        val repository = ProjectInfoRepository.getInstance()
        val savedProjectInfo = repository!!.save(projectInfo)

        val responseBody = Response("Create ProjectInfo successfully! ${savedProjectInfo.projectId}", Gson().toJson(savedProjectInfo))
        return ApiGatewayResponse.build {
            statusCode = 200
            objectBody = responseBody
            headers = Collections.singletonMap<String, String>("X-Powered-By", "AWS Lambda & serverless")
        }
    }

}