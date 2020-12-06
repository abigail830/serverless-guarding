package com.serverless.infrastructure

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride
import com.serverless.model.ProjectWithS3
import org.apache.logging.log4j.LogManager
import java.io.IOException


class ProjectInfoRepository{
    companion object {
        private val LOG = LogManager.getLogger(ProjectInfoRepository::class.java)

        private var repository: ProjectInfoRepository? = ProjectInfoRepository()

        private val dbAdapter = DynamoDBAdapter.getInstance();
        private val mapperConfig = DynamoDBMapperConfig.builder()
                .withTableNameOverride(TableNameOverride("project-info-with-s3"))
                .build()
        private val dbMapper = dbAdapter?.createDbMapper(mapperConfig)
    }


    @Throws(IOException::class)
    fun save(projectWithS3: ProjectWithS3): ProjectWithS3 {
        var projectWithS3InfoPO = ProjectWithS3InfoPO(projectWithS3)
        dbMapper!!.save(projectWithS3InfoPO)
        LOG.info("ProjectWithS3InfoPO saved: $projectWithS3InfoPO")
        return projectWithS3InfoPO.toProjectWithS3()
    }

}



