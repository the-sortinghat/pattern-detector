package com.sortinghat.pattern_detector.api.plugins

import com.sortinghat.pattern_detector.domain.model.ServiceRepository
import com.sortinghat.pattern_detector.domain.services.DatabasePerServiceDetector
import com.sortinghat.pattern_detector.domain.services.MetricCollector
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(serviceRepository: ServiceRepository) {
    routing {
        get("/systems/{slug}/patterns") {
            val slugParam = call.parameters["slug"]
                ?: call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = mapOf("error" to "slug must be provided")
                )

            val services = serviceRepository.findAllOfSystem(slugParam as String)

            val metricCollector = MetricCollector()
            val databasePerServiceDetector = DatabasePerServiceDetector()

            services.forEach { it.accept(metricCollector) }
            services.forEach { it.accept(databasePerServiceDetector) }

            val dbps = databasePerServiceDetector.getResults()

            val patterns = mapOf("DatabasePerService" to dbps)

            call.respond(
                status = HttpStatusCode.OK,
                message = mapOf("patterns" to patterns)
            )
        }
    }
}