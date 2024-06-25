package com.usvision.analyses.analyzer

import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class NumberOfDependents(
    private val asyncDependenciesOfMicroservice: AsyncDependenciesOfMicroservice,
    private val syncDependenciesOfMicroservice: SyncDependenciesOfMicroservice
) : Measurer() {
    private val INT_TYPE_NAME = Int::class.qualifiedName!!
    private val UNIT_DEPENDENTS = "dependents"

    private val counters: MutableMap<Visitable, Measure> = mutableMapOf()

    override fun getResults(): Map<Visitable, Measure> = counters

    override fun visit(microservice: Microservice) {
        val asyncDependents = asyncDependenciesOfMicroservice.getResults().filter { (ms, _) ->
            (ms as Microservice).getSubscribedChannels().any { it in microservice.getPublishChannels() }
        }.count()

        val syncDependents = syncDependenciesOfMicroservice.getResults().filter { (ms, _) ->
            (ms as Microservice).getConsumedOperations().any { it in microservice.getExposedOperations() }
        }.count()

        val count = asyncDependents + syncDependents

        counters[microservice] = Count(
            value = count,
            type = INT_TYPE_NAME, unit = UNIT_DEPENDENTS
        )
    }
}