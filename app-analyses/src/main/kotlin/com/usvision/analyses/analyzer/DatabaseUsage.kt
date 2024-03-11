package com.usvision.analyses.analyzer

import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class DatabaseUsage : RelationshipsAnalyzer() {
    private val usages: MutableMap<Visitable,MutableSet<Relationship>> = mutableMapOf()

    override fun getResults(): Map<Visitable, Set<Relationship>> = usages

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