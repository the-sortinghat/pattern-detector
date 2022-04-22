package com.sortinghat.pattern_detector

import com.sortinghat.pattern_detector.api.plugins.configureHTTP
import com.sortinghat.pattern_detector.api.plugins.configureRouting
import com.sortinghat.pattern_detector.api.plugins.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureRouting()
}