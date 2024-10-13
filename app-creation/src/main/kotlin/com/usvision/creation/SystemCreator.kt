package com.usvision.creation

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.operations.Operation
import com.usvision.model.systemcomposite.System

typealias CompanySystemDTO = CompanySystem
typealias SystemDTO = System
typealias MicroserviceDTO = Microservice
typealias DatabaseDTO = Database

class SystemCreator(
    private val systemAggregateStorage: SystemAggregateStorage
) {

    fun createCompanySystem(companySystem: CompanySystemDTO, fatherSystemName: String? = null): CompanySystemDTO {
        checkIfSystemAlreadyExists(companySystem.name)

        fatherSystemName?.also {
            val fatherSystem = systemAggregateStorage.getCompanySystem(fatherSystemName)

            return@createCompanySystem fatherSystem?.let {
                fatherSystem.addSubsystem(companySystem)
                systemAggregateStorage.save(fatherSystem)
            } ?: throw Exception("A System Of Systems with name $fatherSystemName does not exist")
        }

        return systemAggregateStorage.save(companySystem)
    }

    fun createMicroservice(microservice: MicroserviceDTO, fatherSystemName: String? = null): SystemDTO {
        checkIfSystemAlreadyExists(microservice.name)

        fatherSystemName?.also {
            val fatherSystem = systemAggregateStorage.getCompanySystem(fatherSystemName)

            return@createMicroservice fatherSystem?.let {
                fatherSystem.addSubsystem(microservice)
                systemAggregateStorage.save(fatherSystem)
            } ?: throw Exception("A System Of Systems with name $fatherSystemName does not exist")
        }

        return systemAggregateStorage.save(microservice)
    }

    fun addNewDatabaseConnectionToMicroservice(
        database: DatabaseDTO,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        it.addDatabaseConnection(database)
        systemAggregateStorage.save(it)
    }

    fun addOperationsToMicroservice(
        exposedOperations: List<Operation>,
        consumedOperations: List<Operation>,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        exposedOperations.forEach {
            operation ->  it.exposeOperation(operation)
        }

        consumedOperations.forEach {
            operation -> it.consumeOperation(operation)
        }

        systemAggregateStorage.save(it)
    }

    fun addMessageChannelsToMicroservice(
        publishMessageChannels: List<MessageChannel>,
        subscribedMessageChannels: List<MessageChannel>,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        publishMessageChannels.forEach {
            operation ->  it.addPublishChannel(operation)
        }

        subscribedMessageChannels.forEach {
            operation ->  it.addPublishChannel(operation)
        }

        systemAggregateStorage.save(it)
    }

    private fun getExistingMicroservice(
        microserviceName: String
    ) = systemAggregateStorage.getMicroservice(microserviceName)
        ?: throw Exception("A Microservice with name $microserviceName does not exist")

    private fun checkIfSystemAlreadyExists(name: String) {
        systemAggregateStorage.getSystem(name)?.also {
            throw Exception("A system with name $name already exists")
        }
    }
}