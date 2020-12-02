package com.serverless.infra


import com.amazonaws.AmazonClientException
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder
import com.amazonaws.services.kinesis.model.PutRecordsRequest
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry
import com.amazonaws.services.kinesis.model.PutRecordsResult
import com.google.gson.Gson
import com.serverless.Handler
import com.serverless.model.StreamFileRecord
import org.apache.logging.log4j.LogManager
import java.io.File
import java.nio.ByteBuffer
import java.util.*
import kotlin.math.floor


class KinesisHandler {
    companion object {
        private val LOG = LogManager.getLogger(Handler::class.java)
    }

    fun streamFile(localRepoPath: String, repoUrl: String, branch: String): Map<String, String> {
        val clientBuilder = AmazonKinesisClientBuilder.standard()
        clientBuilder.region = "us-east-1"

        //clientBuilder.credentials = DefaultAWSCredentialsProviderChain.getInstance()
        //clientBuilder.clientConfiguration = config
        val kinesisClient = clientBuilder.build()

        val putRecordsRequest = PutRecordsRequest()
        putRecordsRequest.streamName = "raw-repo"

        var index = 0;
        File(localRepoPath).walk().filter { it.isFile }.forEach {
            val putRecordsRequestEntryList: MutableList<PutRecordsRequestEntry> = ArrayList()
            val putRecordsRequestEntry = PutRecordsRequestEntry()

            val streamFileRecordJson: String = Gson().toJson(
                    StreamFileRecord(repoUrl, branch, it.canonicalPath, it.readBytes()))
            putRecordsRequestEntry.data = ByteBuffer.wrap(streamFileRecordJson.toByteArray())
            putRecordsRequestEntry.partitionKey = "PartitionKey-" + floor((index / 5).toDouble()).toInt()
            putRecordsRequestEntryList.add(putRecordsRequestEntry)
            LOG.info("Add file to shard(${putRecordsRequestEntry.partitionKey}): ${it.canonicalPath}")

            //put every 5 files in one shard
            if (index++ % 5 == 0) {
                putRecordsRequest.setRecords(putRecordsRequestEntryList)
                val putRecordsResult: PutRecordsResult = kinesisClient.putRecords(putRecordsRequest)
                LOG.info("--- Put Result $putRecordsResult")
            }
        }

        return mapOf("stream_name" to "raw-repo", "stream_size" to "$index")
    }

}