package com.usvision.web

import com.usvision.web.configuration.*
import io.ktor.server.application.Application

fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val reportSupervisor = configureReports()

    configureCORS()
    configureSerialization()
    configureRouting(reportSupervisor)
    configureExceptionHandling()
}