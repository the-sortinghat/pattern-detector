package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.CoincidenceOfMicroservices
import com.usvision.analyses.analyzer.Relationship
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class SimilarService(
    private val coincidenceAnalyser: CoincidenceOfMicroservices
) : Detector() {
    companion object {
        const val NUM_COINCIDENCES_THRESHOLD: Int = 3
    }

    private var instances: Set<SimilarServiceInstance> = emptySet()
    private lateinit var DepResults: Map<Visitable, List<Pair<Relationship, String>>>
    private var microservicePairs = mutableSetOf<Pair<Microservice, Microservice>>()
    private var coincidenceTypes = mutableMapOf<Pair<Microservice, Microservice>, MutableSet<String>>()

    override fun collectMetrics() {
        DepResults = coincidenceAnalyser.getDetailedResults()
    }

    override fun combineMetric() {
        DepResults.forEach { (visitable, relationships) ->
            if (visitable is Microservice) {
                relationships.forEach { (relationship, type) ->
                    if (relationship.with is Microservice) {
                        val pair = Pair(visitable, relationship.with)
                        microservicePairs.add(pair)
                        coincidenceTypes.getOrPut(pair) { mutableSetOf() }.add(type)
                    }
                }
            }
        }

        val coincidences = microservicePairs.filter { pair ->
            (coincidenceTypes[pair]?.size ?: 0) >= NUM_COINCIDENCES_THRESHOLD
        }.toSet()

        instances = coincidences.map { (microservice1, microservice2) ->
            SimilarServiceInstance(setOf(microservice1, microservice2), coincidenceTypes[Pair(microservice1, microservice2)] ?: emptySet())
        }.toSet()
    }

    override fun getInstances(): Set<ArchitectureInsight> = instances
}