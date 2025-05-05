package com.usvision.web.configuration

import com.usvision.creation.SystemAggregateStorage
import com.usvision.persistence.repositorybuilder.DBRepositoryProvider
import com.usvision.persistence.repositorybuilder.MongoDBRepositoryProvider
import com.usvision.reports.SystemRepository
import io.ktor.server.application.*

fun Application.configureDatabaseConnection(): Pair<SystemRepository, SystemAggregateStorage> {
    val host = environment.config.property("persistence.host").getString()
    val port = environment.config.property("persistence.port").getString()
    val user = environment.config.property("persistence.username").getString()
    val pass = environment.config.property("persistence.password").getString()
    val dbName = environment.config.property("persistence.database_name").getString()

    val repoProvider: DBRepositoryProvider = MongoDBRepositoryProvider()

    return repoProvider.run {
        connectTo(host)
        setPort(port)
        withCredentials(user, pass)
        setDatabase(dbName)
        Pair(getRepository(), getAggregateStorage())
    }

}