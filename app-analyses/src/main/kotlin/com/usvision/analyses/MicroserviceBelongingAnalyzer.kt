package com.usvision.analyses

import com.usvision.model.CompanySystem
import com.usvision.model.Microservice
import com.usvision.model.Visitable

class MicroserviceBelongingAnalyzer : RelationshipAnalyzer {
    private val parentOf: MutableMap<Visitable, Relationship> = mutableMapOf()

    override fun getResults(): Map<Visitable, Relationship> = parentOf

    override fun visit(companySystem: CompanySystem) {
        val parentRelationship = Relationship(with = companySystem)
        companySystem
            .getSubsystemSet()
            .filterIsInstance<Microservice>()
            .forEach { parentOf[it] = parentRelationship }

        companySystem
            .getSubsystemSet()
            .filter { it !is Microservice }
            .forEach { it.accept(this) }
    }

    override fun visit(microservice: Microservice) {}
}