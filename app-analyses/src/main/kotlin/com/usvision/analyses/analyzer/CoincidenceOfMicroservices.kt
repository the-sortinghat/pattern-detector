package com.usvision.analyses.analyzer

import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class CoincidenceOfMicroservices (
    private val syncDependenciesOfMicroservice: SyncDependenciesOfMicroservice
) : RelationshipAnalyzer() {

    private val msCoincidences: MutableMap<Visitable, Relationship> = mutableMapOf()

    override fun getResults(): Map<Visitable, Relationship> = msCoincidences

    override fun visit(microservice: Microservice) {
        val syncResults = syncDependenciesOfMicroservice.getResults()
        val operationConsumers: MutableMap<String, MutableList<Microservice>> = mutableMapOf()

        syncResults.keys.filterIsInstance<Microservice>().forEach { ms ->
            ms.getConsumedOperations().forEach { operation ->
                operationConsumers.getOrPut(operation.toString()) { mutableListOf() }.add(ms)
            }
        }

        operationConsumers.forEach { (_, microservices) ->
            if (microservices.size > 1) { // More than one microservice consumes the same operation
                microservices.forEach { consumer ->
                    microservices.filter { it != consumer }.forEach { otherConsumer ->
                        val relationship = Relationship(with = otherConsumer) // Assuming Relationship constructor or factory method
                        msCoincidences[consumer] = relationship
                    }
                }
            }
        }
    }
}