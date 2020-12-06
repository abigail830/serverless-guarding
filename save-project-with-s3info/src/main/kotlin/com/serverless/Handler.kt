package com.serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.gson.Gson
import com.serverless.model.ProjectWithS3
import org.apache.logging.log4j.LogManager
import java.util.*


class Handler : RequestHandler<Map<String, Any>, String> {
    companion object {
        private val LOG = LogManager.getLogger(Handler::class.java)
    }

    override fun handleRequest(input: Map<String, Any>, context: Context): String {
        LOG.info("received: $input")

        val projectWithS3 = getProjectWithS3(input)

        val responseBody = Response("With $projectWithS3", input)
        return Gson().toJson(responseBody)
    }

    fun getProjectWithS3(input: Map<String, Any>): ProjectWithS3? {
        val inputString = input["input"] as String
        return Gson().fromJson<ProjectWithS3>(inputString, ProjectWithS3::class.java)
    }


}