package com.usvision.web.configuration

import com.usvision.creation.SystemCreator
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.reports.ReportSupervisor
import com.usvision.web.dto.*
import com.usvision.web.exceptions.MissingRequiredPathParameterException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(reportSupervisor: ReportSupervisor, systemCreator: SystemCreator) {
    val defaultPreset = environment.config.property("reports.default_preset_name").getString()

    routing {
        route("/microservice") {
            post {
                val microservice = call.receive<SystemRequestDTO>().toMicroservice()
                val createMicroserviceResult = systemCreator.createMicroservice(microservice)

                call.respond(
                    message = createMicroserviceResult.toSystemResponseDTO(),
                    status = HttpStatusCode.Created
                )
            }
            post("/{name}/database") {
                val microserviceName: String = call.parameters["name"]
                    ?: throw MissingRequiredPathParameterException("name", "String")

                val databaseDTO = call.receive<PostgreSQL>()
                val addDatabaseResult = systemCreator.addNewDatabaseConnectionToMicroservice(databaseDTO, microserviceName)

                call.respond(
                    message = addDatabaseResult.toMicroserviceResponseDTO(),
                    status = HttpStatusCode.Created
                )
            }

            post("/{name}/rest-endpoints") {
                val microserviceName: String = call.parameters["name"]
                    ?: throw MissingRequiredPathParameterException("name", "String")
                val restEndpoints = call.receive<RestEndpointsRequestDTO>()

                val addOperationsResult = systemCreator.addOperationsToMicroservice(
                    exposedOperations = restEndpoints.exposedOperations,
                    consumedOperations = restEndpoints.consumedOperations,
                    microserviceName = microserviceName
                )

                call.respond(
                    message = addOperationsResult.toMicroserviceResponseDTO(),
                    status = HttpStatusCode.Created
                )
            }

            post("/{name}/message-channels") {
                val microserviceName: String = call.parameters["name"]
                    ?: throw MissingRequiredPathParameterException("name", "String")

                val messageChannels = call.receive<MessageChannelsRequestDTO>()


                val addMessageChannelsResult = systemCreator.addMessageChannelsToMicroservice(
                    publishMessageChannels = messageChannels.publishMessageChannels,
                    subscribedMessageChannels = messageChannels.subscribedMessageChannels,
                    microserviceName = microserviceName
                )

                call.respond(
                    message = addMessageChannelsResult.toMicroserviceResponseDTO(),
                    status = HttpStatusCode.Created
                )
            }
        }

        route("/systems") {
            post {
                val companySystem = call.receive<SystemRequestDTO>().toCompanySystem()
                val createCompanySystemResult = systemCreator.createCompanySystem(companySystem)

                call.respond(
                    message = createCompanySystemResult.toCompanySystemResponseDTO(),
                    status = HttpStatusCode.Created
                )
            }

            post("/{name}/microservice") {
                val systemName: String = call.parameters["name"]
                    ?: throw MissingRequiredPathParameterException("name", "String")

                val microservice = call.receive<SystemRequestDTO>().toMicroservice()
                val createMicroserviceResult = systemCreator.createMicroservice(microservice, systemName)

                call.respond(
                    message = createMicroserviceResult.toSystemResponseDTO(),
                    status = HttpStatusCode.Created
                )
            }

            post("/{name}/companySubsystem") {
                val systemName: String = call.parameters["name"]
                    ?: throw MissingRequiredPathParameterException("name", "String")

                val companySystem = call.receive<SystemRequestDTO>().toCompanySystem()
                val createCompanySystemResult = systemCreator.createCompanySystem(companySystem, systemName)

                call.respond(
                    message = createCompanySystemResult.toCompanySystemResponseDTO(),
                    status = HttpStatusCode.Created
                )
            }

            get("/{name}/reports") {
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