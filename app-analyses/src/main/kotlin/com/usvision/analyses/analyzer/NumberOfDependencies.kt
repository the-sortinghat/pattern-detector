package com.usvision.analyses.analyzer

import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class NumberOfDependencies : Measurer() {
    private val INT_TYPE_NAME = Int::class.qualifiedName!!
    private val UNIT_DEPENDENCIES = "dependencies"

    private val counters: MutableMap<Visitable, Measure> = mutableMapOf()

    override fun getResults(): Map<Visitable, Measure> = counters

    override fun visit(microservice: Microservice) {
        val count = microservice.getConsumedOperations().size +
                microservice.getSubscribedChannels().size +
                microservice.getDatabases().size

        counters[microservice] = Count(
            value = count,
            type = INT_TYPE_NAME, unit = UNIT_DEPENDENCIES
        )
    }
}