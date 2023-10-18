package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.Measure
import com.usvision.analyses.analyzer.MicroservicesOfModule
import com.usvision.analyses.analyzer.NumberOfMicroservicesInAHost
import com.usvision.analyses.analyzer.Relationship
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import com.usvision.model.visitor.Visitable

class SingleServicePerHost(
    private val coHostedMsv: MicroservicesOfModule,
    private val nMsvPerHost: NumberOfMicroservicesInAHost
) : Detector() {
    private lateinit var instances: Set<SingleServicePerHostInstance>
    private lateinit var nMsvcInHost: Map<Visitable, Measure>
    private lateinit var cohostedServices: Map<Visitable, Set<Relationship>>

    override fun collectMetrics() {
        nMsvcInHost = nMsvPerHost.getResults()
        cohostedServices = coHostedMsv.getResults()
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
            .map { SingleServicePerHostInstance.of(it) }
            .toSet()
    }

    override fun getInstances(): Set<ArchitectureInsight> = instances
}