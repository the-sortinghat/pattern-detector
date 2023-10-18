package com.usvision.web.configuration

import com.usvision.persistence.repositorybuilder.DBRepositoryProvider
import com.usvision.persistence.repositorybuilder.MongoDBRepositoryProvider
import com.usvision.reports.ReportSupervisor
import io.ktor.server.application.*
import io.ktor.server.config.*


fun Application.configureReports(): ReportSupervisor {
    val host = environment.config.property("persistence.host").getString()
    val port = environment.config.property("persistence.port").getString()
    val user = environment.config.property("persistence.username").getString()
    val pass = environment.config.property("persistence.password").getString()
    val dbName = environment.config.property("persistence.database_name").getString()

    val presets = parsePresetsConfig(environment.config.config("reports.presets"))

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
        presets = presets
    )
}

fun parsePresetsConfig(presetsConfig: ApplicationConfig): Map<String, Set<String>> {
    val presets = mutableMapOf<String, Set<String>>()
    val configMap = presetsConfig.toMap()

    configMap.keys.forEach { presetName ->
        val maybeDetectorsList = configMap[presetName]!!

        if (maybeDetectorsList !is List<*>) presets[presetName] = emptySet()
        else presets[presetName] = maybeDetectorsList.map { it.toString() }.toSet()
    }

    return presets
}
