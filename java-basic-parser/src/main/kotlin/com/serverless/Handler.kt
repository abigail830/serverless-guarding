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

        if(s3Info.srcFileKey.endsWith(".java")){
            val s3Client: AmazonS3 = AmazonS3ClientBuilder.defaultClient()
            val s3Object: S3Object = s3Client.getObject(GetObjectRequest(
                    s3Info.srcBucket, s3Info.srcFileKey))
            val objectData: InputStream = s3Object.objectContent
            LOG.info(objectData)
        }else{
            LOG.info("Ignore non java files: $s3Info")
        }


        return "200 OK"
    }

}