package com.usvision.analyses.analyzer

import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import com.usvision.model.visitor.Visitable

class NumberOfMicroservicesInAHost : Measurer() {
    private val INT_TYPE_NAME = Int::class.qualifiedName!!
    private val UNIT_MICROSERVICES = "microservices"
    private val counts = mutableMapOf<Visitable,Measure>()

    override fun getResults(): Map<Visitable, Measure> = counts

    override fun visit(microservice: Microservice) {
        microservice.module.accept(this)

        counts[microservice.module] = Count(
            value = (counts[microservice.module]!!.value as Int) + 1,
            unit = UNIT_MICROSERVICES, type = INT_TYPE_NAME
        )
    }

    override fun visit(module: Module) {
        if (module !in counts)
            counts[module] = Count(
                value = 0, type = INT_TYPE_NAME,
                unit = UNIT_MICROSERVICES
            )
    }
}