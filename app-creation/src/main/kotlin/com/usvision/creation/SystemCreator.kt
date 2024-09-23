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
    private val systemRepository: SystemRepository
) {

    fun createCompanySystem(companySystem: CompanySystemDTO): CompanySystemDTO {
        checkIfSystemAlreadyExists(companySystem.name)
        return systemRepository.save(companySystem) as CompanySystemDTO
    }

    fun createSubsystem(system: SystemDTO, fatherSystemName: String? = null): SystemDTO {
        checkIfSystemAlreadyExists(system.name)

        fatherSystemName?.also {
            val fatherSystem = systemRepository.getSystemOfSystems(fatherSystemName)

            return@createSubsystem fatherSystem?.let {
                fatherSystem.addSubsystem(system)
                systemRepository.save(fatherSystem)
            } ?: throw Exception("A System Of Systems with name $fatherSystemName does not exist")
        }

        return systemRepository.save(system)
    }

    fun createMicroservice(
        microservice: MicroserviceDTO,
        fatherSystemName: String? = null
    ) = createSubsystem(microservice, fatherSystemName) as MicroserviceDTO

    fun addNewDatabaseConnectionToMicroservice(
        database: DatabaseDTO,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        it.addDatabaseConnection(database)
        systemRepository.save(it)
    }

    fun addExposedOperationsToMicroservice(
        exposedOperations: List<Operation>,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        exposedOperations.forEach {
            operation ->  it.exposeOperation(operation)
        }
        systemRepository.save(it)
    }


    fun addConsumedOperationsToMicroservice(
        exposedOperations: List<Operation>,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        exposedOperations.forEach {
            operation ->  it.exposeOperation(operation)
        }
        systemRepository.save(it)
    }


    fun addPublishChannelsToMicroservice(
        messageChannels: List<MessageChannel>,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        messageChannels.forEach {
            operation ->  it.addPublishChannel(operation)
        }
        systemRepository.save(it)
    }

    fun addSubscribedChannelsOperationsToMicroservice(
        messageChannels: List<MessageChannel>,
        microserviceName: String
    ) = getExistingMicroservice(microserviceName).let {
        messageChannels.forEach {
            operation ->  it.addSubscribedChannel(operation)
        }
        systemRepository.save(it)
    }

    private fun getExistingMicroservice(
        microserviceName: String
    ) = systemRepository.getSystem(
        microserviceName
    ) as MicroserviceDTO? ?: throw Exception("A Microservice with name $microserviceName does not exist")

    private fun checkIfSystemAlreadyExists(name: String) {
        systemRepository.getSystem(name)?.also {
            throw Exception("A system with name $name already exists")
        }
    }
}