package com.usvision.web.dto

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import com.usvision.model.domain.databases.PostgreSQL
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.model.systemcomposite.System
import kotlinx.serialization.Serializable

@Serializable
data class SystemRequestDTO(
    val name: String,
    val subsystems: Set<SystemRequestDTO>? = null,
    val module: Module? = null,
    val exposedOperations: MutableSet<RestEndpoint>? = null,
    val consumedOperations: MutableSet<RestEndpoint>? = null,
    val databases: MutableSet<PostgreSQL>? = null,
    val publishChannels: MutableSet<MessageChannel>? = null,
    val subscribedChannels: MutableSet<MessageChannel>? = null,
) {
    fun toMicroservice(): Microservice {
        val microservice = module?.let { Microservice(name, module) } ?: Microservice(name)

        databases?.forEach { database -> microservice.addDatabaseConnection(database) }

        exposedOperations?.forEach { operation -> microservice.exposeOperation(operation) }
        consumedOperations?.forEach { operation -> microservice.consumeOperation(operation) }

        publishChannels?.forEach { messageChannel -> microservice.addPublishChannel(messageChannel) }
        subscribedChannels?.forEach { messageChannel -> microservice.addSubscribedChannel(messageChannel) }

        return microservice
    }

    fun toCompanySystem(): CompanySystem {
        val companySystem = CompanySystem(name)

        subsystems?.forEach { subsystemRequestDTO ->
            companySystem.addSubsystem(subsystemRequestDTO.toSystem())
        }

        return companySystem
    }

    private fun toSystem(): System {
        subsystems ?: return this.toCompanySystem()

        return this.toMicroservice()
    }
}
