package com.sortinghat.pattern_detector

import com.sortinghat.pattern_detector.api.plugins.configureHTTP
import com.sortinghat.pattern_detector.api.plugins.configureRouting
import com.sortinghat.pattern_detector.api.plugins.configureSerialization
import com.sortinghat.pattern_detector.db.DatabaseAdapterImpl
import com.sortinghat.pattern_detector.db.ServiceRepositoryImpl
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    val url = environment.config.property("database.url").getString()
    val driver = environment.config.property("database.driver").getString()
    val user = environment.config.property("database.user").getString()
    val password = environment.config.property("database.password").getString()

    val dbAdapter = DatabaseAdapterImpl(url, driver, user, password)

    dbAdapter.connect()
    val serviceRepository = ServiceRepositoryImpl()

    configureSerialization()
    configureHTTP()
    configureRouting(serviceRepository)
}