package com.serverless.api

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.serverless.ApiGatewayResponse
import com.serverless.Response
import com.serverless.infrastructure.ProjectInfoRepository
import org.apache.logging.log4j.LogManager
import java.util.*

class RemoveHandler : RequestHandler<Map<String, Any>, ApiGatewayResponse> {
    companion object {
        private val LOG = LogManager.getLogger(RemoveHandler::class.java)
    }

    override fun handleRequest(input: Map<String, Any>, context: Context): ApiGatewayResponse {
        val pathParameters = input["pathParameters"] as Map<String, String>?
        val projectId = pathParameters!!["id"]
        LOG.info("Received ProjectId to be removed : $projectId")

        val repository = ProjectInfoRepository.getInstance()
        return if (repository!!.delete(projectId)) {
            val responseBody = Response("Remove ProjectInfo successfully!", projectId ?: "")
            ApiGatewayResponse.build {
                statusCode = 200
                objectBody = responseBody
                headers = Collections.singletonMap<String, String>("X-Powered-By", "AWS Lambda & serverless")
            }
        } else {
            val responseBody = Response("Fail to remove ProjectInfo!", projectId ?: "")
            ApiGatewayResponse.build {
                statusCode = 422
                objectBody = responseBody
                headers = Collections.singletonMap<String, String>("X-Powered-By", "AWS Lambda & serverless")
            }
        }


    }


}