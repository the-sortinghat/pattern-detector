package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.PatternDetector
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*
import com.sortinghat.pattern_detector.domain.model.patterns.SingleServicePerHost

class SingleServicePerHostDetector(
    maxOperationsPerService: Int = 8
) : Visitor, PatternDetector {

    private val maxOperationsPerService: Int
    private val visited = mutableSetOf<Visitable>()
    private val serviceCandidates = mutableSetOf<Service>()
    private val moduleCandidates = mutableListOf<Module>()

    init {
        this.maxOperationsPerService = maxOperationsPerService
    }

    override fun visit(service: Service) {
        if (service in visited) return

        visited.add(service)
        val hasFewOperations = service.get(Metrics.OPERATIONS_OF_SERVICE) < maxOperationsPerService
        if (hasFewOperations) serviceCandidates.add(service)

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
        val singleService = module.get(Metrics.SERVICES_PER_MODULE) == 1
        if (singleService) moduleCandidates.add(module)

        module.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(channel: MessageChannel) {
    }

    override fun getResults(): Set<SingleServicePerHost> {
        return moduleCandidates
            .filter { module ->
                module.services.first() in serviceCandidates
            }
            .map { module ->
                val service = module.services.first()
                SingleServicePerHost.from(service)
            }
            .toSet()
    }
}