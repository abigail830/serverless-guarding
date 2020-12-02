package com.serverless.infrastructure

import org.jasypt.encryption.StringEncryptor
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig


class DefaultEncryptor : StringEncryptor {
    var encryptor: PooledPBEStringEncryptor

    init {
        val encryptor = PooledPBEStringEncryptor()
        val config = SimpleStringPBEConfig()
        config.password = System.getenv("ENCRYPTION_KEY") ?: "PASSWORD"
        config.algorithm = "PBEWithMD5AndDES"
        config.setKeyObtentionIterations("10000")
        config.setPoolSize("1")
        config.providerName = "SunJCE"
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator")
        config.stringOutputType = "base64"
        encryptor.setConfig(config)
        this.encryptor = encryptor
    }

    override fun encrypt(message: String?): String {
        return encryptor.encrypt(message)
    }

    override fun decrypt(message: String?): String {
        return encryptor.decrypt(message)
    }

}