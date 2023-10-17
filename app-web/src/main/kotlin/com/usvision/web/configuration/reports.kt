package com.usvision.web.configuration

import com.usvision.persistence.repositorybuilder.DBRepositoryProvider
import com.usvision.persistence.repositorybuilder.MongoDBRepositoryProvider
import com.usvision.reports.ReportSupervisor
import io.ktor.server.application.*

fun Application.configureReports(): ReportSupervisor {
    val host = environment.config.property("persistence.host").getString()
    val port = environment.config.property("persistence.port").getString()
    val user = environment.config.property("persistence.username").getString()
    val pass = environment.config.property("persistence.password").getString()
    val dbName = environment.config.property("persistence.database_name").getString()

    val presetsConfig = environment.config.config("reports.presets")

    val repoProvider: DBRepositoryProvider = MongoDBRepositoryProvider()

    val systemRepository = repoProvider.run {
        connectTo(host)
        setPort(port)
        withCredentials(user, pass)
        setDatabase(dbName)
        getRepository()
    }

    return ReportSupervisor(
        systemRepository,
        presets = presetsConfig.toMap() as Map<String,Set<String>>
    )
}