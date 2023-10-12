package com.usvision.persistence.repositorybuilder

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.usvision.persistence.repositories.MongoSystemRepository

class MongoDBRepositoryProvider : DBRepositoryProvider, DBConnectionProvider<MongoDatabase> {
    private var host: String? = "localhost"
    private var port: String? = null
    private var password: String? = null
    private var username: String? = null
    private var databaseName: String? = null
    private var db: MongoDatabase? = null

    override fun getConnection(): MongoDatabase {
        doConnect()
        return db!!
    }

    override fun connectTo(host: String): MongoDBRepositoryProvider {
        this.host = host
        return this
    }

    override fun setPort(port: String): MongoDBRepositoryProvider {
        this.port = port
        return this
    }

    override fun withCredentials(username: String, password: String): MongoDBRepositoryProvider {
        this.username = username
        this.password = password
        return this
    }

    override fun setDatabase(databaseName: String): MongoDBRepositoryProvider {
        this.databaseName = databaseName
        return this
    }

    override fun getRepository(): MongoSystemRepository {
        doConnect()
        return MongoSystemRepository(db!!)
    }

    private fun doConnect() {
        if (db == null)
            db = MongoClient
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