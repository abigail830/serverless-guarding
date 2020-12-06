package com.serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.gson.Gson
import com.serverless.infrastructure.ProjectInfoRepository
import com.serverless.model.ProjectWithS3
import org.apache.logging.log4j.LogManager
import java.security.InvalidParameterException
import java.util.*


class Handler : RequestHandler<Map<String, Any>, String> {
    companion object {
        private val LOG = LogManager.getLogger(Handler::class.java)
        private val projectInfoRepository = ProjectInfoRepository()
    }

    override fun handleRequest(input: Map<String, Any>, context: Context): String {
        LOG.info("received: $input")

        val projectWithS3 = getProjectWithS3(input)
        projectWithS3!!.let {
            projectInfoRepository.save(it)
            val responseBody = Response("With $projectWithS3", input)
            return Gson().toJson(responseBody)
        }
    }

    fun getProjectWithS3(input: Map<String, Any>): ProjectWithS3? {
        val inputString = input["input"] as String
        return Gson().fromJson<ProjectWithS3>(inputString, ProjectWithS3::class.java)
    }


}