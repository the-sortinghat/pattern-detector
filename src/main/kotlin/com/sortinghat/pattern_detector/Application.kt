package com.sortinghat.pattern_detector

import com.sortinghat.pattern_detector.api.plugins.configureHTTP
import com.sortinghat.pattern_detector.api.plugins.configureRouting
import com.sortinghat.pattern_detector.api.plugins.configureSerialization
import io.ktor.application.Application
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureHTTP()
    configureSerialization()
    configureRouting()
}