package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*

class MetricCollector : Visitor {

    private val visited = mutableSetOf<Visitable>()

    override fun visit(service: Service) {
        if (service in visited) return

        visited.add(service)

        service.exposedOperations.forEach { _ -> service.increase(Metrics.OPERATIONS_OF_SERVICE) }
        service.usages.forEach { _ -> service.increase(Metrics.DATABASES_USED_BY_SERVICE) }
        service.consumedOperations.forEach { _ -> service.increase(Metrics.SYNC_DEPENDENCY) }

        service.children().forEach { child ->
            child.accept(visitor = this)
        }
    }

    override fun visit(operation: Operation) {
    }

    override fun visit(database: Database) {
        if (database in visited) return

        visited.add(database)

        database.usages.forEach { _ -> database.increase(Metrics.CLIENTS_OF_DATABASE) }

        database.children().forEach { child ->
            child.accept(visitor = this)
        }
    }

    override fun visit(usage: DatabaseUsage) {
        if (usage in visited) return

        visited.add(usage)
        usage.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(module: Module) {
        if (module in visited) return

        visited.add(module)

        module.children().forEach {
            module.increase(Metrics.SERVICES_PER_MODULE)
            it.accept(visitor = this)
        }
    }

    override fun visit(channel: MessageChannel) {
    }
}