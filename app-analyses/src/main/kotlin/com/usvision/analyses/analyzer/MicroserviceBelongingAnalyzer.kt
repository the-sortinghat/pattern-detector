package com.usvision.analyses.analyzer

import com.usvision.model.domain.CompanySystem
import com.usvision.model.domain.databases.Database
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class MicroserviceBelongingAnalyzer : RelationshipAnalyzer {
    private val parentOf: MutableMap<Visitable, Relationship> = mutableMapOf()

    override fun getResults(): Map<Visitable, Relationship> = parentOf

    override fun visit(companySystem: CompanySystem) {
        val parentRelationship = Relationship(with = companySystem)
        companySystem
            .getSubsystemSet()
            .filterIsInstance<Microservice>()
            .forEach { parentOf[it] = parentRelationship }
    }

    override fun visit(microservice: Microservice) {}

    override fun visit(database: Database) {}
}