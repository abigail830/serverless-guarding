package com.serverless

class S3Info(input: Map<String, Object>) {
    var srcBucket: String = input["srcBucket"].toString()
    var srcFileKey: String = input["srcFileKey"].toString()

    override fun toString(): String {
        return "S3Info(srcBucket='$srcBucket', srcFileKey='$srcFileKey')"
    }

}