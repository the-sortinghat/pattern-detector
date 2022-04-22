package com.sortinghat.pattern_detector.api.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

@Suppress("unused")
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}