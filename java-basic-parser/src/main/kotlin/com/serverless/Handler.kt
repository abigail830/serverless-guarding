package com.serverless

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3Object
import org.apache.logging.log4j.LogManager
import java.io.InputStream

class Handler:RequestHandler<Map<String, Object>, String> {
    companion object {
        private val LOG = LogManager.getLogger(Handler::class.java)
    }

    override fun handleRequest(input:Map<String, Object>, context:Context):String {
        LOG.info("received: $input")

        val s3Info = S3Info(input)
        val s3Client: AmazonS3 = AmazonS3ClientBuilder.defaultClient()
        val s3Object: S3Object = s3Client.getObject(GetObjectRequest(
                s3Info.srcBucket, s3Info.srcBucket))
        val objectData: InputStream = s3Object.objectContent
//
//        //parse
        LOG.info(objectData)

        return "200 OK"
    }

}