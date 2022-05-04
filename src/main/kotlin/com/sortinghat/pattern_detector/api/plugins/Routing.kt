package com.sortinghat.pattern_detector.api.plugins

import com.sortinghat.pattern_detector.api.PatternsInSystemPayload
import com.sortinghat.pattern_detector.domain.model.ServiceRepository
import com.sortinghat.pattern_detector.domain.services.detectionWorkflow
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(serviceRepository: ServiceRepository) {
    routing {
        get("/systems/{slug}/patterns") {
            val systemSlug = call.parameters["slug"]
                ?: return@get call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = mapOf("error" to "slug must be provided")
                )

            val detections = detectionWorkflow(systemSlug, serviceRepository)

            call.respond(
                status = HttpStatusCode.OK,
                message = PatternsInSystemPayload.from(systemSlug, detections)
            )
        }
    }
}