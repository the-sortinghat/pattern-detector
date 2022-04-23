package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*

class MetricCollector : Visitor {

    private val visited = mutableSetOf<Visitable>()

    override fun visit(service: Service) {
        if (service in visited) return

        visited.add(service)
        service.operations.forEach { op ->
            service.increase(Metrics.OPERATIONS_OF_SERVICE)
            op.accept(visitor = this)
        }

        service.usages.forEach { usage ->
            service.increase(Metrics.DATABASES_USED_BY_SERVICE)
            usage.accept(visitor = this)
        }
    }

    override fun visit(operation: Operation) {
    }

    override fun visit(database: Database) {
        if (database in visited) return

        visited.add(database)
        database.usages.forEach { usage ->
            database.increase(Metrics.CLIENTS_OF_DATABASE)
            usage.accept(visitor = this)
        }
    }

    override fun visit(usage: DatabaseUsage) {
        if (usage in visited) return

        visited.add(usage)
        usage.service.accept(visitor = this)
        usage.database.accept(visitor = this)
    }
}