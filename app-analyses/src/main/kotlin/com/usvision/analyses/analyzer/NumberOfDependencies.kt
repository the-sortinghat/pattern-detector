package com.usvision.analyses.analyzer

import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.Database
import com.usvision.model.visitor.Visitable
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.operations.Operation

class NumberOfDependencies : Measurer() {
    private val INT_TYPE_NAME = Int::class.qualifiedName!!
    private val UNIT_DEPENDENCIES = "dependencies"

    private val publishers: MutableMap<MessageChannel, MutableSet<Microservice>> = mutableMapOf()
    private val subscribers: MutableMap<MessageChannel, MutableSet<Microservice>> = mutableMapOf()
    private val msDatabase: MutableMap<Database, MutableSet<Microservice>> = mutableMapOf()
    private val operations: MutableMap<Operation, MutableSet<Microservice>> = mutableMapOf()
    private val results: MutableMap<Visitable, Measure> = mutableMapOf()


    override fun getResults(): Map<Visitable, Measure> {
        val allMicroservices = (publishers.values.flatten() + subscribers.values.flatten() + msDatabase.values.flatten() + operations.values.flatten())


        allMicroservices.forEach { visitable ->
            val microservice = visitable
            val operationCount = microservice.getConsumedOperations().size
            val producerCount = countProducers(microservice)
            val databaseCount = microservice.getDatabases().size

            val totalCount = operationCount + producerCount + databaseCount

            this.results[microservice] = Count(
                value = totalCount,
                type = INT_TYPE_NAME,
                unit = UNIT_DEPENDENCIES
            )
        }

        return this.results
    }

    override fun visit(microservice: Microservice) {
        microservice.getPublishChannels().forEach { pubChannel ->
            if (pubChannel !in this.publishers) {
                this.publishers[pubChannel] = mutableSetOf()
            }

            this.publishers[pubChannel]?.add(microservice)
        }

        microservice.getSubscribedChannels().forEach { subChannel ->
            if (subChannel !in this.subscribers) {
                this.subscribers[subChannel] = mutableSetOf()
            }

            this.subscribers[subChannel]?.add(microservice)
        }

        microservice.getDatabases().forEach { database ->
            if (database !in this.msDatabase) {
                this.msDatabase[database] = mutableSetOf()
            }

            this.msDatabase[database]?.add(microservice)
        }

        microservice.getConsumedOperations().forEach { operation ->
            if (operation !in this.operations) {
                this.operations[operation] = mutableSetOf()
            }

            this.operations[operation]?.add(microservice)
        }

        // Initialize the Count object for the microservice with a value of 0 to avoid null pointer exceptions
        this.results[microservice] = Count(value = 0, type = INT_TYPE_NAME, unit = UNIT_DEPENDENCIES)
    }

    private fun countProducers(microservice: Microservice): Int {
        return microservice.getSubscribedChannels().sumOf { channel ->
            publishers[channel]?.size ?: 0
        }
    }

}