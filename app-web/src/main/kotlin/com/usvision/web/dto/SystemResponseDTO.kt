package com.usvision.web.dto

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.model.domain.operations.Operation
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.model.exceptions.UnknownOperationClassException
import com.usvision.model.exceptions.UnknownSystemClassException
import com.usvision.model.exceptions.UnknownDatabaseClassException
import com.usvision.model.systemcomposite.System
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
sealed interface SystemResponseDTO

@SerialName("CompanySystem")
@Serializable
data class CompanySystemResponseDTO(
    val name: String,
    val subsystems: Set<SystemResponseDTO>? = setOf(),
): SystemResponseDTO

@SerialName("Microservice")
@Serializable
data class MicroserviceResponseDTO(
    val name: String,
    val module: Module,
    val exposedOperations: Set<RestEndpoint>,
    val consumedOperations: Set<RestEndpoint>,
    val databases: Set<PostgreSQL>,
    val publishChannels: Set<MessageChannel>,
    val subscribedChannels: Set<MessageChannel>
): SystemResponseDTO

fun CompanySystem.toCompanySystemResponseDTO() = CompanySystemResponseDTO(
    this.name,
    this.getSubsystemSet().toSystemResponseDTOSet()
)

fun Microservice.toMicroserviceResponseDTO() = MicroserviceResponseDTO(
    name = this.name,
    module = this.module,
    exposedOperations = this.getExposedOperations().toRestEndpointSet(),
    consumedOperations = this.getConsumedOperations().toRestEndpointSet(),
    databases = this.getDatabases().toPostgreSQLSet(),
    publishChannels = this.getPublishChannels(),
    subscribedChannels = this.getSubscribedChannels()
)

fun System.toSystemResponseDTO(): SystemResponseDTO = when (this) {
    is Microservice -> this.toMicroserviceResponseDTO()
    is CompanySystem -> this.toCompanySystemResponseDTO()
    else -> throw UnknownSystemClassException(this.name, "SystemResponseDTO")
}

private fun Set<Database>.toPostgreSQLSet(): Set<PostgreSQL> = this.map { database ->
    when (database) {
        is PostgreSQL -> database
        else -> throw UnknownDatabaseClassException("SystemResponseDTO")
    }
}.toSet()

private fun Set<Operation>.toRestEndpointSet(): Set<RestEndpoint> = this.map { operation ->
    when (operation) {
        is RestEndpoint -> operation
        else -> throw UnknownOperationClassException("SystemResponseDTO")
    }
}.toSet()

private fun  Set<System>.toSystemResponseDTOSet(): Set<SystemResponseDTO> = this.map { it.toSystemResponseDTO() }.toSet()
