package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.PatternDetector
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*

@Suppress("unused")
class DatabasePerServiceDetector : Visitor, PatternDetector {

    private val nOperationsThreshold = 8

    private val visited = mutableSetOf<Visitable>()
    private val serviceCandidates = mutableSetOf<Service>()
    private val databaseCandidates = mutableSetOf<Database>()

    override fun visit(service: Service) {
        if (service in visited) return

        visited.add(service)
        val hasFewOperations = service.get(Metrics.OPERATIONS_OF_SERVICE) < nOperationsThreshold

        if (hasFewOperations) serviceCandidates.add(service)

        service.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(operation: Operation) {
    }

    override fun visit(database: Database) {
        if (database in visited) return

        visited.add(database)
        val singleClient = database.get(Metrics.CLIENTS_OF_DATABASE) == 1

        if (singleClient) databaseCandidates.add(database)

        database.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(usage: DatabaseUsage) {
        if (usage in visited) return

        visited.add(usage)
        usage.children().forEach { it.accept(visitor = this) }
    }

    override fun getResults(): Set<DatabasePerService> {
        val occurrences = mutableSetOf<DatabasePerService>()

        serviceCandidates.forEach { service ->
            val database = databaseCandidates.find { db ->
                db.usages.any { usage -> usage.service == service }
            } ?: return@forEach

            occurrences.add(DatabasePerService.from(service, database))
        }

        return occurrences
    }
}