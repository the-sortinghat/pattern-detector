package com.sortinghat.pattern_detector.api.plugins

import com.sortinghat.pattern_detector.api.PatternsInSystemPayload
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
                ?: return@get call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = mapOf("error" to "slug must be provided")
                )

            val services = serviceRepository.findAllOfSystem(slugParam)

            val collector = MetricCollector()
            val dbpsDetector = DatabasePerServiceDetector()

            services.forEach { it.accept(collector) }
            services.forEach { it.accept(dbpsDetector) }

            val body = PatternsInSystemPayload.create(
                system = slugParam,
                databasePerServices = dbpsDetector.getResults()
            )

            call.respond(
                status = HttpStatusCode.OK,
                message = body
            )
        }
    }
}