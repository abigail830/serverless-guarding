package com.serverless.model

data class StreamFileRecord(val repoUrl: String, val branch: String, val path: String, val content: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StreamFileRecord

        if (repoUrl != other.repoUrl) return false
        if (branch != other.branch) return false
        if (path != other.path) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = repoUrl.hashCode()
        result = 31 * result + branch.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + content.contentHashCode()
        return result
    }
}