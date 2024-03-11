package com.usvision.persistence.repositorybuilder

interface DBConnectionBuilder {
    fun connectTo(host: String): DBRepositoryProvider
    fun setPort(port: String): DBRepositoryProvider
    fun withCredentials(username: String, password: String): DBRepositoryProvider
    fun setDatabase(databaseName: String): DBRepositoryProvider
}
