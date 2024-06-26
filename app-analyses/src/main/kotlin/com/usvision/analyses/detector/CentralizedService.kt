package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.Measure
import com.usvision.analyses.analyzer.NumberOfDependents
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class CentralizedService(
    private val numberOfDependents: NumberOfDependents
) : Detector() {
    companion object {
        const val DEPENDENT_THRESHOLD: Int = 3
    }

    private lateinit var dependentResults: Map<Visitable, Measure>
    private var instances: MutableSet<CentralizedServiceInstance> = mutableSetOf()

    override fun collectMetrics() {
        dependentResults = numberOfDependents.getResults()
    }

    override fun combineMetric() {
        instances = dependentResults
            .filter { (_, measure) -> measure.value as Int > DEPENDENT_THRESHOLD }
            .map { (ms, measure) -> CentralizedServiceInstance(ms as Microservice, measure.value as Int) }
            .toMutableSet()
    }

    override fun getInstances(): Set<ArchitectureInsight> = instances
}
