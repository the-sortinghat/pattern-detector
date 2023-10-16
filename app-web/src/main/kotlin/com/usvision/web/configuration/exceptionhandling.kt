package com.usvision.web.configuration

import com.usvision.reports.exceptions.ClassIsNotDetectorException
import com.usvision.reports.exceptions.UnknownPresetException
import com.usvision.web.exceptions.MissingRequiredPathParameterException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<MissingRequiredPathParameterException> { call: ApplicationCall, mrppe: MissingRequiredPathParameterException ->
            call.respond(
                message = mapOf("error" to mrppe.localizedMessage),
                status = HttpStatusCode.BadRequest
            )
        }
        exception<ClassIsNotDetectorException> { call: ApplicationCall, cinde: ClassIsNotDetectorException ->
            call.respond(
                message = mapOf("error" to cinde.localizedMessage),
                status = HttpStatusCode.BadRequest
            )
        }
        exception<UnknownPresetException> { call: ApplicationCall, upe: UnknownPresetException ->
            call.respond(
                message = mapOf("error" to upe.localizedMessage),
                status = HttpStatusCode.BadRequest
            )
        }
    }
}