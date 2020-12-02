package com.serverless.api

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.gson.Gson
import com.serverless.ApiGatewayResponse
import com.serverless.Response
import com.serverless.infrastructure.ProjectInfoRepository
import org.apache.logging.log4j.LogManager
import java.util.*


class GetHandler:RequestHandler<Map<String,Any>, ApiGatewayResponse> {
    companion object {
        private val LOG = LogManager.getLogger(GetHandler::class.java)
    }

    override fun handleRequest(input:Map<String,Any>, context:Context): ApiGatewayResponse {
        LOG.debug("Received input : $input")

        val pathParameters = input["pathParameters"] as Map<String, String>?
        val projectId = pathParameters!!["id"]
        LOG.info("Received ProjectId to be searched : $projectId")

        val service = ProjectInfoRepository.getInstance()
        val result = service!!.get(projectId)
        LOG.debug("$result")

        return if(result != null){
            val responseBody = Response("Get ProjectInfo successfully!", Gson().toJson(result))
            ApiGatewayResponse.build {
                statusCode = 200
                objectBody = responseBody
                headers = Collections.singletonMap<String, String>("X-Powered-By", "AWS Lambda & serverless")
            }
        }else{
            val responseBody = Response("ProjectInfo not found!", projectId ?: "")
            ApiGatewayResponse.build {
                statusCode = 404
                objectBody = responseBody
                headers = Collections.singletonMap<String, String>("X-Powered-By", "AWS Lambda & serverless")
            }
        }

    }

}