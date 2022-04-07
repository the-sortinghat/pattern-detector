package com.sortinghat.pattern_detector.api.plugins

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.routing.get

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, message = mapOf("message" to "hello, world"))
        }
    }
}