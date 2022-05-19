package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.PatternDetector
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*
import com.sortinghat.pattern_detector.domain.model.patterns.APIComposition

class APICompositionDetector(
    maxOperationsPerService: Int = 8,
    minComposedServices: Int = 2
) : Visitor, PatternDetector {

    private val maxOperationsPerService: Int
    private val minComposedServices: Int

    private val visited = mutableSetOf<Visitable>()
    private val candidates = mutableSetOf<Service>()
    private val exposedBy: MutableMap<Operation, Service> = mutableMapOf()

    init {
        this.maxOperationsPerService = maxOperationsPerService
        this.minComposedServices = minComposedServices
    }

    override fun getResults(): Set<APIComposition> {
        return candidates
            .filter(::readsFromManyServices)
            .map(APIComposition.Companion::from)
            .toSet()
    }

    private fun readsFromManyServices(service: Service): Boolean {
        val dependencies = mutableSetOf<Service>()
        service.consumedOperations.forEach { op -> dependencies.add(exposedBy[op]!!) }
        return dependencies.size >= minComposedServices
    }

    override fun visit(service: Service) {
        if (service in visited) return

        visited.add(service)

        val hasFewOperations = service.get(Metrics.OPERATIONS_OF_SERVICE) < maxOperationsPerService
        val dependsOnMany = service.get(Metrics.SYNC_DEPENDENCY) >= 2
        val exposedQuery = service.exposedOperations.count { op -> op.verb == HttpVerb.GET } >= 1

        if (hasFewOperations && dependsOnMany && exposedQuery) candidates.add(service)

        service.exposedOperations.forEach { operation -> exposedBy[operation] = service }

        service.children().forEach { it.accept(visitor = this) }
    }


    override fun visit(operation: Operation) {
        if (operation in visited) return

        visited.add(operation)
        operation.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(database: Database) {
        if (database in visited) return

        visited.add(database)

        database.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(usage: DatabaseUsage) {
        if (usage in visited) return

        visited.add(usage)

        usage.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(module: Module) {
        if (module in visited) return

        visited.add(module)

        module.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(channel: MessageChannel) {
        if (channel in visited) return

        visited.add(channel)
        channel.children().forEach { it.accept(visitor = this) }
    }
}