package com.usvision.analyses

import com.usvision.model.CompanySystem
import com.usvision.model.Database
import com.usvision.model.Microservice
import com.usvision.model.Visitable

class DatabaseUsage : RelationshipsAnalyzer {
    private val usages: MutableMap<Visitable,MutableSet<Relationship>> = mutableMapOf()

    override fun getResults(): Map<Visitable, Set<Relationship>> = usages

    override fun visit(companySystem: CompanySystem) {}

    override fun visit(microservice: Microservice) {
        microservice.getDatabases().forEach { db ->
            db.accept(this)
            usages[db]!!.add(Relationship(with = microservice))
        }
    }

    override fun visit(database: Database) {
        if (database !in usages) {
            usages[database] = mutableSetOf()
        }
    }
}