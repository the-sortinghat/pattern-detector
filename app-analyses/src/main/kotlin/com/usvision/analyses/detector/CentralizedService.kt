package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.Measure
import com.usvision.analyses.analyzer.NumberOfDependents
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class CentralizedService(
    private val numberOfDependents: NumberOfDependents
) : Detector() {
    private lateinit var dependentResults: Map<Visitable, Measure>
    private var instances: MutableSet<CentralizedServiceInstance> = mutableSetOf()

    override fun collectMetrics() {
        dependentResults = numberOfDependents.getResults()
    }

    override fun combineMetric() {
        val average = dependentResults.values.map { it.value as Int }.average()
        dependentResults.forEach { (microservice, measure) ->
            if (measure.value as Int > average) {
                this.instances.add(CentralizedServiceInstance(microservice as Microservice, measure.value as Int))
            }
        }
    }

    override fun getInstances(): Set<ArchitectureInsight> = instances
}