package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.CoincidenceOfMicroservices
import com.usvision.analyses.analyzer.Relationship
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class SimilarService(
    private val coincidenceAnalyser: CoincidenceOfMicroservices
) : Detector() {
    companion object {
        const val NUM_SYNC_COINCIDENCES_THRESHOLD: Int = 1
    }

    private var instances: Set<SimilarServiceInstance> = emptySet()
    private lateinit var syncDepResults: Map<Visitable, Relationship>
    private var microservicePairs = mutableSetOf<Pair<Microservice, Microservice>>()

    override fun collectMetrics() {
        syncDepResults = coincidenceAnalyser.getResults()
    }

    override fun combineMetric() {
        syncDepResults.forEach { (visitable, relationship) ->
            if (visitable is Microservice && relationship.with is Microservice) {
                microservicePairs.add(Pair(visitable, relationship.with))
            }
        }

        val coincidences = microservicePairs.groupBy { it }
            .filter { (_, pairs) -> pairs.size >= NUM_SYNC_COINCIDENCES_THRESHOLD }
            .flatMap { it.value }
            .toSet()

        instances = coincidences.map { (microservice1, microservice2) ->
            SimilarServiceInstance(setOf(microservice1, microservice2))
        }.toSet()
    }

    override fun getInstances(): Set<ArchitectureInsight> = instances
}