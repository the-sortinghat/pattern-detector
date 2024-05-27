package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.Measure
import com.usvision.analyses.analyzer.NumberOfDependencies
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class DependentHub(
    private val numberOfDependencies: NumberOfDependencies
) : Detector() {
    private lateinit var dependencyResults: Map<Visitable, Measure>
    private lateinit var instances: Set<DependentHubInstance>

    override fun collectMetrics() {
        dependencyResults = numberOfDependencies.getResults()
    }

    override fun combineMetric() {
        val averageDependencies = dependencyResults.values.map { it.value as Int }.average()
        instances = dependencyResults
            .filter { (_, measure) -> measure.value as Int > averageDependencies }
            .map { (ms, measure) -> DependentHubInstance(ms as Microservice, measure.value as Int) }
            .toSet()
    }

    override fun getInstances(): Set<ArchitectureInsight> = instances
}