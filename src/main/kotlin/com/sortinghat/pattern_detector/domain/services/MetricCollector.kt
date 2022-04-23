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
    }

    override fun visit(operation: Operation) {
    }

    override fun visit(database: Database) {
    }

    override fun visit(usage: DatabaseUsage) {
    }
}