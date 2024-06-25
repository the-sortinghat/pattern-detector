package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.*
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class ApiComposition(
    private val nOps: NumberOfExposedOperations,
    private val nReadingOps: NumberOfReadingExposedOperations,
    private val syncDeps: SyncDependenciesOfMicroservice,
) : Detector() {
    companion object {
        const val MAX_COHESIVE_THRESHOLD: Int = 9
        const val MIN_READING_OPS_THRESHOLD: Int = 1
        const val MIN_DEPENDENCIES_THRESHOLD: Int = 2
    }

    private lateinit var candidates: Set<Microservice>
    private lateinit var deps: Map<Visitable, Set<Relationship>>
    private lateinit var nrdops: Map<Visitable, Measure>
    private lateinit var nops: Map<Visitable, Measure>

    override fun collectMetrics() {
        nops = nOps.getResults()
        nrdops = nReadingOps.getResults()
        deps = syncDeps.getResults()
    }

    override fun combineMetric() {
        val poolOfMicroservices = mutableMapOf<Microservice, Pair<Int, Int>>()
        initialize(poolOfMicroservices)
        accountNops(poolOfMicroservices)
        accountReadingOps(poolOfMicroservices)
        filterCohesiveAndReadable(poolOfMicroservices)
        filterDependingOnAtLeast2()
    }

    private fun filterDependingOnAtLeast2() {
        candidates = candidates
            .filter { ms -> ms in deps && deps[ms]!!.size >= MIN_DEPENDENCIES_THRESHOLD }
            .toSet()
    }

    private fun filterCohesiveAndReadable(poolOfMicroservices: MutableMap<Microservice, Pair<Int, Int>>) {
        candidates = poolOfMicroservices
            .filter { (_, counts) -> counts.first <= MAX_COHESIVE_THRESHOLD && counts.second >= MIN_READING_OPS_THRESHOLD }
            .keys
    }

    private fun accountReadingOps(poolOfMicroservices: MutableMap<Microservice, Pair<Int, Int>>) {
        nrdops.keys.forEach { ms ->
            if (ms is Microservice) {
                val micro = ms
                val numRds = nrdops[ms]!!.value as Int
                val pair = poolOfMicroservices[micro]!!
                poolOfMicroservices[micro] = Pair(pair.first, numRds)
            }
        }
    }

    private fun accountNops(poolOfMicroservices: MutableMap<Microservice, Pair<Int, Int>>) {
        nops.keys.forEach { ms ->
            if (ms is Microservice) {
                val micro = ms
                val numOps = nops[ms]!!.value as Int
                val pair = poolOfMicroservices[micro]!!
                poolOfMicroservices[micro] = Pair(numOps, pair.second)
            }
        }
    }

    private fun initialize(poolOfMicroservices: MutableMap<Microservice, Pair<Int, Int>>) {
        (nops.keys + nrdops.keys).forEach { ms ->
            if (ms is Microservice) {
                val micro = ms
                if (micro !in poolOfMicroservices) {
                    poolOfMicroservices[micro] = Pair(0, 0)
                }
            }
        }
    }

    override fun getInstances(): Set<ArchitectureInsight> {
        return candidates
            .map { ms ->
                ApiCompositionInstance.of(
                    composer = ms,
                    relationships = deps[ms]!!
                )
            }
            .toSet()
    }
}