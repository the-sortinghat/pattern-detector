package com.usvision.web.configuration

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
    }
}