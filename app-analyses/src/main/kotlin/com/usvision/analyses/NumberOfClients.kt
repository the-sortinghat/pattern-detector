package com.usvision.analyses

import com.usvision.model.CompanySystem
import com.usvision.model.Database
import com.usvision.model.Microservice
import com.usvision.model.Visitable

class NumberOfClients : Measurer {
    private val INT_TYPE = Int::class.qualifiedName!!
    private val counters: MutableMap<Visitable, Measure> = mutableMapOf()

    override fun getResults(): Map<Visitable, Measure> = counters

    override fun visit(companySystem: CompanySystem) {}

    override fun visit(microservice: Microservice) {
        microservice.getDatabases().forEach {db ->
            db.accept(this)
            val currentCount = counters[db] as Count
            counters[db] = currentCount.copy(
                value = (currentCount.value as Int) + 1
            )
        }
    }

    override fun visit(database: Database) {
        if (database !in counters) {
            counters[database] = Count(
                value = 0,
                type = INT_TYPE, unit = "clients"
            )
        }
    }
}