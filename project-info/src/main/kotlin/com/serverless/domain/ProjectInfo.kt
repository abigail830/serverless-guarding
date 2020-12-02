package com.serverless.domain

class ProjectInfo() {

    var projectId: String? = null
    var repoUrl: String? = null
    var branch: String? = null
    var userName: String? = null
    var password: String? = null

    constructor(projectId: String?, repoUrl: String?, branch: String?,
                userName: String?, password: String?) : this() {
        this.projectId = projectId
        this.repoUrl = repoUrl
        this.branch = branch
        this.userName = userName
        this.password = password
    }

    override fun toString(): String {
        return "ProjectInfo(projectId=$projectId, repoUrl=$repoUrl, branch=$branch, userName=$userName)"
    }


}