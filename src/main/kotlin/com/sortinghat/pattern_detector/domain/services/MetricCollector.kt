package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*

class MetricCollector : Visitor {

    private val visited = mutableSetOf<Visitable>()

    override fun visit(service: Service) {
        if (service in visited) return

        visited.add(service)

        service.children().forEach { child ->
            when (child) {
                is Operation -> service.increase(Metrics.OPERATIONS_OF_SERVICE)
                is DatabaseUsage -> service.increase(Metrics.DATABASES_USED_BY_SERVICE)
            }

            child.accept(visitor = this)
        }
    }

    override fun visit(operation: Operation) {
    }

    override fun visit(database: Database) {
        if (database in visited) return

        visited.add(database)
        database.children().forEach { child ->
            if (child is DatabaseUsage) database.increase(Metrics.CLIENTS_OF_DATABASE)

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
}