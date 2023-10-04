package com.usvision.analyses

import com.usvision.model.CompanySystem
import com.usvision.model.Microservice
import com.usvision.model.Visitable

class NumberOfExposedOperations : Measurer {
    private val INT_TYPE_NAME = Int::class.qualifiedName!!
    private val UNIT_OPERATIONS = "operations"

    private val counters: MutableMap<Visitable, Measure> = mutableMapOf()

    override fun getResults(): Map<Visitable, Measure> = counters

    override fun visit(companySystem: CompanySystem) {
        counters[companySystem] = Count(
            value = companySystem.getExposedOperations().size,
            type = INT_TYPE_NAME, unit = UNIT_OPERATIONS
        )
    }

    override fun visit(microservice: Microservice) {
        counters[microservice] = Count(
            value = microservice.getExposedOperations().size,
            type = INT_TYPE_NAME, unit = UNIT_OPERATIONS
        )
    }
}