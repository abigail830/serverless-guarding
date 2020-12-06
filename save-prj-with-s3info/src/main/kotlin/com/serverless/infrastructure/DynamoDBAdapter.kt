package com.serverless.infrastructure

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig

class DynamoDBAdapter private constructor() {

    companion object {
        private var client: AmazonDynamoDB? = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .build()

        private var dbAdapter: DynamoDBAdapter? = null
        fun getInstance(): DynamoDBAdapter? {
            if (dbAdapter == null) dbAdapter = DynamoDBAdapter()
            return dbAdapter
        }
    }
    private var mapper: DynamoDBMapper? = null
    fun createDbMapper(mapperConfig: DynamoDBMapperConfig?): DynamoDBMapper? {
        if (client != null) mapper = DynamoDBMapper(client, mapperConfig)
        return mapper
    }


}