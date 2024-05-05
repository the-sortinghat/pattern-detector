package com.usvision.analyses.analyzer

import com.usvision.model.domain.Microservice
import com.usvision.model.domain.operations.Operation
import com.usvision.model.visitor.Visitable

class SyncDependenciesOfMicroservice : RelationshipsAnalyzer() {
    private val producers = mutableMapOf<Operation, Microservice>()
    private val consumers = mutableMapOf<Operation, MutableSet<Microservice>>()

    override fun getResults(): Map<Visitable, Set<Relationship>> {
        val dependencies = mutableMapOf<Visitable, MutableSet<Relationship>>()

        consumers.keys.forEach { op ->
            val exposer = producers[op]
            if (exposer != null) {
                consumers[op]?.forEach { cons ->
                    if (cons !in dependencies)
                        dependencies[cons] = mutableSetOf()

                    dependencies[cons]?.add(Relationship(with = exposer))
                }
            }
        }

        return dependencies
    }

    override fun visit(microservice: Microservice) {
        microservice.getConsumedOperations().forEach { op ->
            if (op !in consumers) {
                consumers[op] = mutableSetOf()
            }

            consumers[op]?.add(microservice)
        }

        microservice.getExposedOperations().forEach { op ->
            producers[op] = microservice
        }
    }
}