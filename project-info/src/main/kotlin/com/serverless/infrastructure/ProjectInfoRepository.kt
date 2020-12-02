package com.serverless.infrastructure

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.serverless.domain.ProjectInfo
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.util.*


class ProjectInfoRepository private constructor() {
    companion object {
        private val LOG = LogManager.getLogger(ProjectInfoRepository::class.java)

        private var repository: ProjectInfoRepository? = null
        fun getInstance(): ProjectInfoRepository? {
            if (repository == null) repository = ProjectInfoRepository()
            return repository
        }

        private val dbAdapter = DynamoDBAdapter.getInstance();
        private val mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride("project-info"))
                .build()
        private val dbMapper = dbAdapter?.createDbMapper(mapperConfig)
    }

    @Throws(IOException::class)
    fun list(): List<ProjectInfo>? {
        val scanExp = DynamoDBScanExpression()
        return dbMapper!!.scan(ProjectInfoPO::class.java, scanExp).map { it.toProjectInfo() }
    }

    @Throws(IOException::class)
    operator fun get(id: String?): ProjectInfo? {
        return getPO(id)?.toProjectInfo()

    }

    fun getPO(id: String?): ProjectInfoPO? {
        val attributeValues: HashMap<String, AttributeValue> = HashMap()
        attributeValues[":id"] = AttributeValue().withS(id)
        val queryExp: DynamoDBQueryExpression<ProjectInfoPO> = DynamoDBQueryExpression<ProjectInfoPO>()
                .withKeyConditionExpression("projectId = :id")
                .withExpressionAttributeValues(attributeValues)
        val result: PaginatedQueryList<ProjectInfoPO> = dbMapper!!.query(ProjectInfoPO::class.java, queryExp)

        return if (result.size > 0) {
            LOG.info("ProjectInfoPO $result[0]")
            result[0]
        } else {
            LOG.info("ProjectInfo Not Found.")
            null
        }
    }

    @Throws(IOException::class)
    fun save(projectInfo: ProjectInfo): ProjectInfo {
        var projectInfoPO = ProjectInfoPO(projectInfo)
        dbMapper!!.save(projectInfoPO)
        LOG.info("ProjectInfoPO saved: $projectInfoPO")
        return projectInfoPO.toProjectInfo()
    }

    @Throws(IOException::class)
    fun delete(id: String?): Boolean {
        // get product if exists
        var projectInfoPO = getPO(id)
        if (projectInfoPO == null) {
            LOG.info("ProjectInfoPO does not exist for delete: $projectInfoPO")
            return false
        }
        dbMapper!!.delete(projectInfoPO)
        LOG.info("ProjectInfoPO deleted: $projectInfoPO")
        return true
    }
}



