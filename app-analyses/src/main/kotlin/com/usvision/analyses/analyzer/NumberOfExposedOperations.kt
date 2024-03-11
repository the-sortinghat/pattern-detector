package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.operations.Operation
import com.usvision.model.domain.operations.RestEndpoint
import com.usvision.model.visitor.Visitable

open class NumberOfExposedOperations : Measurer() {
    private val INT_TYPE_NAME = Int::class.qualifiedName!!
    private val UNIT_OPERATIONS = "operations"

    private val counters: MutableMap<Visitable, Measure> = mutableMapOf()

    override fun getResults(): Map<Visitable, Measure> = counters

    override fun visit(companySystem: CompanySystem) {
        val count = companySystem
            .getExposedOperations()
            .filter(this::filterExposedOperations)
            .size

        counters[companySystem] = Count(
            value = count,
            type = INT_TYPE_NAME, unit = UNIT_OPERATIONS
        )
    }

    override fun visit(microservice: Microservice) {
        val count = microservice
            .getExposedOperations()
            .filter(this::filterExposedOperations)
            .size

        counters[microservice] = Count(
            value = count,
            type = INT_TYPE_NAME, unit = UNIT_OPERATIONS
        )
    }

    protected open fun filterExposedOperations(operation: Operation): Boolean = true
}

class NumberOfReadingExposedOperations : NumberOfExposedOperations() {
    override fun filterExposedOperations(operation: Operation): Boolean {
        return operation.isReading()
    }
}

class NumberOfWritingExposedOperations : NumberOfExposedOperations() {
    override fun filterExposedOperations(operation: Operation): Boolean {
        return !operation.isReading()
    }
}