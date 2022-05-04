package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.PatternDetector
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*
import com.sortinghat.pattern_detector.domain.model.patterns.SingleServicePerHost

class SingleServicePerHostDetector : Visitor, PatternDetector {

    private val visited = mutableSetOf<Visitable>()
    private val detections = mutableSetOf<SingleServicePerHost>()

    override fun visit(service: Service) {
        if (service in visited) return

        visited.add(service)

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
        if (singleService) {
            val theService = module.services.first()
            detections.add(SingleServicePerHost.from(theService))
        }

        module.children().forEach { it.accept(visitor = this) }
    }

    override fun getResults(): Set<SingleServicePerHost> {
        return detections
    }
}