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
    private val producers = mutableMapOf<Operation, MutableSet<Microservice>>()
    private val consumers = mutableMapOf<Operation, MutableSet<Microservice>>()
    private val msDatabase: MutableMap<Database, MutableSet<Microservice>> = mutableMapOf()
    private val results: MutableMap<Visitable, Measure> = mutableMapOf()

    override fun getResults(): Map<Visitable, Measure> {
        val allMicroservices = (publishers.values.flatten() + subscribers.values.flatten() + msDatabase.values.flatten() + consumers.values.flatten()).toSet()

        allMicroservices.forEach { visitable ->
            val microservice = visitable
            val consumersCount = countMsConsumedOps(microservice)
            val subscribersCount = countSubscribers(microservice)
            val databaseCount = microservice.getDatabases().size

            val totalCount = consumersCount + subscribersCount + databaseCount

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
            publishers.computeIfAbsent(pubChannel) { mutableSetOf() }.add(microservice)
        }

        microservice.getSubscribedChannels().forEach { subChannel ->
            subscribers.computeIfAbsent(subChannel) { mutableSetOf() }.add(microservice)
        }

        microservice.getDatabases().forEach { database ->
            msDatabase.computeIfAbsent(database) { mutableSetOf() }.add(microservice)
        }

        microservice.getExposedOperations().forEach { operation ->
            producers.computeIfAbsent(operation) { mutableSetOf() }.add(microservice)
        }

        microservice.getConsumedOperations().forEach { operation ->
            consumers.computeIfAbsent(operation) { mutableSetOf() }.add(microservice)
        }

        // Initialize the Count object for the microservice with a value of 0 to avoid null pointer exceptions
        this.results[microservice] = Count(value = 0, type = INT_TYPE_NAME, unit = UNIT_DEPENDENCIES)
    }

    private fun countSubscribers(microservice: Microservice): Int {
        return microservice.getSubscribedChannels().sumOf { channel ->
            publishers[channel]?.size ?: 0
        }
    }

    private fun countMsConsumedOps(microservice: Microservice): Int {
        return microservice.getConsumedOperations().flatMap { operation ->
            producers[operation] ?: emptySet()
        }.distinct().size
    }
}
