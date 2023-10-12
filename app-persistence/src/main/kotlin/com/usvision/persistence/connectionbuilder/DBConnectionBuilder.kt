package com.usvision.persistence.connectionbuilder

interface DBConnectionBuilder<T> {
    fun connectTo(host: String): DBConnectionBuilder<T>
    fun setPort(port: String): DBConnectionBuilder<T>
    fun withCredentials(username: String, password: String): DBConnectionBuilder<T>
    fun setDatabase(databaseName: String): DBConnectionBuilder<T>
    fun build(): T
}
