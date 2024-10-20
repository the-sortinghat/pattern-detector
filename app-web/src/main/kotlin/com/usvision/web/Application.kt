package com.usvision.web

import com.usvision.web.configuration.*
import io.ktor.server.application.Application

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val (databaseRepository, databaseAggregateStorage) = configureDatabaseConnection()
    val systemCreator = configureSystemCreator(databaseAggregateStorage)
    val reportSupervisor = configureReports(databaseRepository)

    configureCORS()
    configureSerialization()
    configureRouting(reportSupervisor, systemCreator)
    configureExceptionHandling()
}