package com.usvision.persistence.connectionbuilder

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase

class MongoDBConnectionBuilder : DBConnectionBuilder<MongoDatabase> {
    private var host: String? = "localhost"
    private var port: String? = null
    private var password: String? = null
    private var username: String? = null
    private var databaseName: String? = null

    override fun connectTo(host: String): MongoDBConnectionBuilder {
        this.host = host
        return this
    }

    override fun setPort(port: String): MongoDBConnectionBuilder {
        this.port = port
        return this
    }

    override fun withCredentials(username: String, password: String): MongoDBConnectionBuilder {
        this.username = username
        this.password = password
        return this
    }

    override fun setDatabase(databaseName: String): MongoDBConnectionBuilder {
        this.databaseName = databaseName
        return this
    }

    override fun build(): MongoDatabase {
        return MongoClient
            .create(connectionString())
            .getDatabase(databaseName!!)
    }

    private fun hasSpecificPort(): Boolean = port != null

    private fun hasCredentials(): Boolean = username != null && password != null

    private fun connectionString(): String {
        var str = "mongodb"

        str += if (!hasSpecificPort()) "+srv://" else "://"
        str += if (hasCredentials()) "$username:$password@" else ""
        str += if (hasSpecificPort()) "$host:$port/" else "$host/"
        str += "?retryWrites=true&w=majority"

        return str
    }
}