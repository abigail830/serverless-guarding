package com.serverless.api

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.gson.Gson
import com.serverless.ApiGatewayResponse
import com.serverless.Response
import com.serverless.infrastructure.ProjectInfoRepository
import org.apache.logging.log4j.LogManager
import java.util.*

class ListHandler : RequestHandler<Map<String, Any>, ApiGatewayResponse> {
    companion object {
        private val LOG = LogManager.getLogger(ListHandler::class.java)
    }

    override fun handleRequest(input: Map<String, Any>, context: Context): ApiGatewayResponse {
        LOG.info("Received ProjectInfo ListAll request")

        val repository = ProjectInfoRepository.getInstance()
        val result = repository!!.list()
        LOG.debug("$result")

        val responseBody = Response("List all ProjectInfo successfully!", Gson().toJson(result))
        return ApiGatewayResponse.build {
            statusCode = 200
            objectBody = responseBody
            headers = Collections.singletonMap<String, String>("X-Powered-By", "AWS Lambda & serverless")
        }
    }

}