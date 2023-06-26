package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.SmellDetector
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*
import com.sortinghat.pattern_detector.domain.model.smells.SharedPersistenceSmell

class SharedPersistenceSmellDetector : Visitor, SmellDetector {

    private val visited = mutableSetOf<Visitable>()
    private val databaseCandidates = mutableSetOf<Database>()
    private val results = mutableMapOf<String, MutableList<String>>()
    private val occurrences = mutableSetOf<SharedPersistenceSmell>()
    private var usageCount: Int = 0


    override fun visit(service: Service) {
        val serviceDatabases = mutableMapOf<Service, MutableSet<Database>>()

        if (service in visited) return
        visited.add(service)
        serviceDatabases.putIfAbsent(service, mutableSetOf())

        service.usages.forEach { usage ->
            val database = usage.database
            serviceDatabases[service]?.add(database)
        }

        service.children().forEach { it.accept(visitor = this) }

        serviceDatabases.forEach { (service, databases) ->
            for (count in databases) {
                usageCount = count.usages.size
            }
            if (usageCount > 1) {
                databases.forEach { database ->
                    results.getOrPut(database.name) { mutableListOf() }.add(service.name)
                }
            }
        }

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

        val singleClient = database.get(Metrics.CLIENTS_OF_DATABASE) == 1

        if (singleClient) databaseCandidates.add(database)
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

    override fun visit(dependency: ServiceDependency) {
        if (dependency in visited) return

        visited.add(dependency)
        dependency.children().forEach { it.accept(visitor = this) }
    }

    override fun getResults(): Set<SharedPersistenceSmell> {
        return results.map { (database, services) ->
            SharedPersistenceSmell(database, services)
        }.toSet()
    }
}
