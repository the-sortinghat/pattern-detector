package com.usvision.analyses.analyzer

import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.MessageChannel
import com.usvision.model.domain.operations.Operation
import com.usvision.model.visitor.Visitable

class CoincidenceOfMicroservices : RelationshipAnalyzer() {
    private val msCoincidences: MutableMap<Visitable, MutableList<Pair<Relationship, String>>> = mutableMapOf()
    private val producers = mutableMapOf<Operation, MutableList<Microservice>>()
    private val consumers = mutableMapOf<Operation, MutableList<Microservice>>()
    private val publishers: MutableMap<MessageChannel, MutableList<Microservice>> = mutableMapOf()
    private val subscribers: MutableMap<MessageChannel, MutableList<Microservice>> = mutableMapOf()
    private val msDatabase: MutableMap<Database, MutableList<Microservice>> = mutableMapOf()

    override fun getResults(): Map<Visitable, Relationship> {
        return msCoincidences.mapValues { entry ->
            entry.value.first().first
        }
    }

    fun getDetailedResults(): Map<Visitable, List<Pair<Relationship, String>>> {
        return msCoincidences.mapValues { entry ->
            entry.value.toList()
        }
    }

    override fun visit(microservice: Microservice) {
        checkCoincidences(microservice, { it.getConsumedOperations().toList() }, consumers, "sync")
        checkCoincidences(microservice, { it.getExposedOperations().toList() }, producers, "sync")
        checkCoincidences(microservice, { it.getPublishChannels().toList() }, publishers, "async")
        checkCoincidences(microservice, { it.getSubscribedChannels().toList() }, subscribers, "async")
        checkCoincidences(microservice, { it.getDatabases().toList() }, msDatabase, "database")
    }

    private fun <T> checkCoincidences(
        microservice: Microservice,
        getDependencies: (Microservice) -> List<T>,
        dependenciesMap: MutableMap<T, MutableList<Microservice>>,
        relationshipType: String
    ) {
        getDependencies(microservice).forEach { dependency ->
            if (dependency !in dependenciesMap) {
                dependenciesMap[dependency] = mutableListOf()
            }
            dependenciesMap[dependency]?.add(microservice)
        }
        dependenciesMap.forEach { (_, microservices) ->
            if (microservices.size > 1) {
                microservices.forEach { ms ->
                    microservices.filter { it != ms }.forEach { otherMs ->
                        val relationship = Relationship(with = otherMs)
                        msCoincidences.getOrPut(ms) { mutableListOf() }.add(Pair(relationship, relationshipType))
                    }
                }
            }
        }
    }
}