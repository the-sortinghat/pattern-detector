package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.*
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import com.usvision.model.visitor.Visitable

class SingleServicePerHost(
    private val coHostedMsv: MicroservicesOfModule,
    private val nMsvPerHost: NumberOfMicroservicesInAHost,
    private val nops: NumberOfExposedOperations,
) : Detector() {
    companion object {
        const val MAX_COHESIVE_THRESHOLD: Int = 9
    }

    private lateinit var instances: Set<SingleServicePerHostInstance>
    private lateinit var nMsvcInHost: Map<Visitable, Measure>
    private lateinit var cohostedServices: Map<Visitable, Set<Relationship>>
    private lateinit var nopsPerService: Map<Visitable, Measure>

    override fun collectMetrics() {
        nMsvcInHost = nMsvPerHost.getResults()
        cohostedServices = coHostedMsv.getResults()
        nopsPerService = nops.getResults()
    }

    override fun combineMetric() {
        instances = nMsvcInHost
            .filter { (_, measure) -> measure.value == 1 }
            .keys
            .map { module ->
                Pair(
                    module as Module,
                    cohostedServices[module]!!.first().with as Microservice
                )
            }
            .filter { (nopsPerService[it.second]!!.value as Int) <= MAX_COHESIVE_THRESHOLD }
            .map { SingleServicePerHostInstance.of(it) }
            .toSet()
    }

    override fun getInstances(): Set<ArchitectureInsight> = instances
}