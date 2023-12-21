package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.AsyncDependenciesOfMicroservice
import com.usvision.analyses.analyzer.Relationship
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class AsyncMessaging(
    private val asyncDependenciesOfMicroservice: AsyncDependenciesOfMicroservice
) : Detector() {
    private val instances: MutableSet<ArchitectureInsight> = mutableSetOf()
    private lateinit var asyncDeps: Map<Visitable, Set<Relationship>>

    override fun collectMetrics() {
        asyncDeps = asyncDependenciesOfMicroservice.getResults()
    }

    override fun combineMetric() {
        asyncDeps.forEach { (cons, producers) ->
            producers.forEach {
                this.instances.add(AsyncMessagingInstance(
                    publisher = it.with as Microservice,
                    subscriber = cons as Microservice
                ))
            }
        }
    }

    override fun getInstances(): Set<ArchitectureInsight> {
        return this.instances
    }
}