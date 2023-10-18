package com.usvision.web.configuration

import com.usvision.reports.ReportSupervisor
import com.usvision.web.exceptions.MissingRequiredPathParameterException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(reportSupervisor: ReportSupervisor) {
    val defaultPreset = environment.config.property("reports.default_preset_name").getString()

    routing {
        route("/systems/{name}/reports") {
            get {
                val systemName: String = call.parameters["name"]
                    ?: throw MissingRequiredPathParameterException("name", "String")

                val detections = call.request.queryParameters.getAll("detections") ?: emptyList()
                val preset = call.request.queryParameters["preset"] ?: defaultPreset

                val report = if (detections.isNotEmpty())
                    reportSupervisor.generateReport(
                        detectorsNames = detections.toSet(),
                        systemName = systemName
                    )
                else
                    reportSupervisor.generateReport(
                        presetName = preset,
                        systemName = systemName
                    )


                call.respond(
                    message = report,
                    status = HttpStatusCode.OK
                )
            }
        }

        route("/info") {
            get("/report_presets") {
                val presets = reportSupervisor.getPresets()
                call.respond(
                    message = mapOf("presets" to presets),
                    status = HttpStatusCode.OK
                )
            }

            get("/detectors") {
                call.respond(
                    message = mapOf("detectors" to reportSupervisor.getDetectors()),
                    status = HttpStatusCode.OK
                )
            }
        }
    }
}