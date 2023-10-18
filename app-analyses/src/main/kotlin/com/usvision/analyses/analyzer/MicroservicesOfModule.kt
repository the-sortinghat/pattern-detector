package com.usvision.analyses.analyzer

import com.usvision.model.domain.Microservice
import com.usvision.model.domain.Module
import com.usvision.model.visitor.Visitable

class MicroservicesOfModule : RelationshipsAnalyzer() {
    private val microservicesIn: MutableMap<Visitable, MutableSet<Relationship>> = mutableMapOf()

    override fun getResults(): Map<Visitable, Set<Relationship>> = microservicesIn

    override fun visit(module: Module) {
        if (module !in microservicesIn)
            microservicesIn[module] = mutableSetOf()
    }

    override fun visit(microservice: Microservice) {
        microservice.module.accept(this)

        microservicesIn[microservice.module]!!.add(
            Relationship(with = microservice)
        )
    }
}