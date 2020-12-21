package com.serverless

import com.amazonaws.regions.Regions
import com.amazonaws.services.lambda.AWSLambdaClientBuilder
import com.amazonaws.services.lambda.model.InvocationType
import com.amazonaws.services.lambda.model.InvokeRequest
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.S3Event
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord
import com.google.gson.GsonBuilder
import org.apache.logging.log4j.LogManager
import java.nio.charset.Charset


class Handler : RequestHandler<SQSEvent, String> {

    companion object {
        private val LOG = LogManager.getLogger(Handler::class.java)
        var gson = GsonBuilder().setPrettyPrinting().create()
        val ignorePaths = listOf<String>(".git")
    }

    override fun handleRequest(sqsEvent: SQSEvent, context: Context): String? {
        val input = gson.toJson(sqsEvent)
        LOG.info("SQS EVENT: $input")

        sqsEvent.records.forEach { record ->
            run {
                try {
                    val s3Event = S3Event.parseJson(record.body)
                    val s3EventNotificationRecord: S3EventNotificationRecord = s3Event.records[0]
                    val srcBucket = s3EventNotificationRecord.s3.bucket.name
                    val srcKey = s3EventNotificationRecord.s3.getObject().urlDecodedKey
                    LOG.info("Going to read S3 Ojbect: $srcBucket | $srcKey")

                    val s3Info = S3Info(srcBucket, srcKey)
                    if (!ignorePaths.contains(srcKey)) {
                        invokeJavaParser(s3Info)
                    } else {
                        LOG.info("$srcKey to be ignore")
                    }

                } catch (e: Exception) {
                    LOG.error("Exception when extract S3 Info: ${e.message}")
                    throw e
                }
            }
        }
        return "200 OK"

    }

    private fun invokeJavaParser(payload: S3Info): String? {
        LOG.info("Going to trigger JavaBasicParser for $payload")
        val functionName = "arn:aws:lambda:us-east-1:008135705340:function:java-basic-parser-dev-parse"
        val payload = gson.toJson(payload)
        val lambdaRequest: InvokeRequest = InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(payload)
        lambdaRequest.setInvocationType(InvocationType.RequestResponse);

        val lambda = AWSLambdaClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build()
        val lambdaResponse = lambda.invoke(lambdaRequest)
        val resultJSON = String(lambdaResponse.payload.array(), Charset.forName("UTF-8"))
        LOG.info("Response from JavaBasicParser: $resultJSON")
        return resultJSON
    }

}