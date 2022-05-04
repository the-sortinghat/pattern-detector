package com.sortinghat.pattern_detector.api.plugins

import com.sortinghat.pattern_detector.api.PatternsInSystemPayload
import com.sortinghat.pattern_detector.domain.model.ServiceRepository
import com.sortinghat.pattern_detector.domain.services.DatabasePerServiceDetector
import com.sortinghat.pattern_detector.domain.services.MetricCollector
import com.sortinghat.pattern_detector.domain.services.SingleServicePerHostDetector
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(serviceRepository: ServiceRepository) {
    routing {
        get("/systems/{slug}/patterns") {
            val slugParam = call.parameters["slug"]
                ?: return@get call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = mapOf("error" to "slug must be provided")
                )

            val services = serviceRepository.findAllOfSystem(slugParam)

            val visitors = mapOf(
                "metrics" to MetricCollector(),
                "dbps" to DatabasePerServiceDetector(),
                "ssph" to SingleServicePerHostDetector()
            )

            visitors.values.forEach { visitor ->
                services.forEach { it.accept(visitor) }
            }

            val body = PatternsInSystemPayload.create(
                system = slugParam,
                databasePerServices = (visitors["dbps"] as DatabasePerServiceDetector).getResults(),
                singleServicePerHost = (visitors["ssph"] as SingleServicePerHostDetector).getResults()
            )

            call.respond(
                status = HttpStatusCode.OK,
                message = body
            )
        }
    }
}