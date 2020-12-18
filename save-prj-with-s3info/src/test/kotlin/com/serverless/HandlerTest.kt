package com.serverless

import org.junit.Test

import org.junit.Assert.*

class HandlerTest {

    @Test
    fun getProjectWithS3_with_all_provided() {
        val input = mapOf("input" to "{\"s3Info\":{\"bucketName\":\"clone-project-to-s3\",\"projectName\":\"parkinglot\",\"s3Folder\":\"parkinglot/1607226848644\"},\"repoUrl\":\"https://github.com/qicaisheng/parkinglot.git\",\"userName\":\"abigail830\",\"password\":\"pwd\",\"branch\":\"master\"}")
        val handler = Handler();
        val result = handler.getProjectWithS3(input)

        assertEquals("https://github.com/qicaisheng/parkinglot.git", result!!.repoUrl)
        assertEquals("abigail830", result.userName)
        assertEquals("master", result.branch)
        assertEquals("pwd", result.password)
        assertEquals("clone-project-to-s3", result.s3Info.bucketName)
        assertEquals("parkinglot", result.s3Info.projectName)
        assertTrue(result.s3Info.s3Folder.startsWith("parkinglot/"))
    }

    @Test
    fun getProjectWithS3_with_partial_provided() {
        val input = mapOf("input" to "{\"s3Info\":{\"bucketName\":\"clone-project-to-s3\"},\"repoUrl\":\"https://github.com/qicaisheng/parkinglot.git\",\"userName\":\"abigail830\",\"branch\":\"master\"}")
        val handler = Handler();
        val result = handler.getProjectWithS3(input)

        assertEquals("https://github.com/qicaisheng/parkinglot.git", result!!.repoUrl)
        assertEquals("abigail830", result.userName)
        assertEquals("master", result.branch)
        assertEquals("clone-project-to-s3", result.s3Info.bucketName)
        assertNull(result.password)
        assertNull(result.s3Info.s3Folder)
        assertNull(result.s3Info.projectName)
    }
}