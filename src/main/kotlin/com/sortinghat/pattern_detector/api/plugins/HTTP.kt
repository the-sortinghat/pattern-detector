package com.sortinghat.pattern_detector.api.plugins

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.http.HttpMethod
import io.ktor.http.HttpHeaders

fun Application.configureHTTP() {
    install(CORS) {

        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.ContentType)
        host("*")

    }
}