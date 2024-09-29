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

    fun createCompanySystem(companySystem: CompanySystemDTO): CompanySystemDTO {
        checkIfSystemAlreadyExists(companySystem.name)
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

    fun addExposedOperationsToMicroservice(
        exposedOperations: List<Operation>,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        exposedOperations.forEach {
            operation ->  it.exposeOperation(operation)
        }
        systemAggregateStorage.save(it)
    }


    fun addConsumedOperationsToMicroservice(
        exposedOperations: List<Operation>,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        exposedOperations.forEach {
            operation ->  it.exposeOperation(operation)
        }
        systemAggregateStorage.save(it)
    }


    fun addPublishChannelsToMicroservice(
        messageChannels: List<MessageChannel>,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        messageChannels.forEach {
            operation ->  it.addPublishChannel(operation)
        }
        systemAggregateStorage.save(it)
    }

    fun addSubscribedChannelsOperationsToMicroservice(
        messageChannels: List<MessageChannel>,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        messageChannels.forEach {
            operation ->  it.addSubscribedChannel(operation)
        }
        systemAggregateStorage.save(it)
    }

    private fun getExistingMicroservice(
        microserviceName: String
    ) = systemAggregateStorage.getSystem(
        microserviceName
    ) as MicroserviceDTO? ?: throw Exception("A Microservice with name $microserviceName does not exist")

    private fun checkIfSystemAlreadyExists(name: String) {
        systemAggregateStorage.getSystem(name)?.also {
            throw Exception("A system with name $name already exists")
        }
    }
}