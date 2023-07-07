package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.PatternDetector
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*
import com.sortinghat.pattern_detector.domain.model.patterns.AsyncMessage
import com.sortinghat.pattern_detector.domain.model.patterns.CQRS

class CQRSDetector(
    asyncMessageOccurrences: Set<AsyncMessage>,
    private val maxOperationsPerService: Int = 8
) : PatternDetector, Visitor {

    private val visited: MutableSet<Visitable> = mutableSetOf()
    private val consumerToPublishers: Map<String, Set<String>>
    private val cohesiveServices: MutableSet<Service> = mutableSetOf()
    private val operationExposedByService: MutableMap<Operation, Service> = mutableMapOf()
    private val queryCandidates: MutableSet<Service> = mutableSetOf()
    private val commandCandidates: MutableSet<Service> = mutableSetOf()

    init {
        consumerToPublishers = asyncMessageOccurrences
            .fold(mutableMapOf()) { acc: MutableMap<String, MutableSet<String>>, occurrence ->
                if (acc[occurrence.subscriber] == null) acc[occurrence.subscriber] = mutableSetOf()
                acc[occurrence.subscriber]!!.add(occurrence.publisher)
                return@fold acc
            }
    }

    override fun getResults(): Set<CQRS> {
        return queryCandidates
            .mapNotNull { queryCandidate ->
                val publishersNames = consumerToPublishers[queryCandidate.name] ?: emptySet()
                val commands = commandCandidates.filter { it.name in publishersNames }.toSet()
                if (commands.isEmpty()) null
                else CQRS.from(queryCandidate, commands)
            }
            .toSet()
    }

    override fun visit(service: Service) {
        if (service in visited) return

        visited.add(service)

        val isCohesive = service.get(Metrics.OPERATIONS_OF_SERVICE) < maxOperationsPerService
        if (isCohesive) cohesiveServices.add(service)

        service.exposedOperations.forEach { operationExposedByService[it] = service }

        service.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(operation: Operation) {
        if (operation in visited) return

        visited.add(operation)

        val writingOperationVerbs = listOf(
            HttpVerb.POST,
            HttpVerb.PUT,
            HttpVerb.DELETE,
            HttpVerb.PATCH
        )

        val parentService: Service = operationExposedByService[operation]!!

        if (operation.verb == HttpVerb.GET) {
            if (parentService in cohesiveServices) queryCandidates.add(parentService)
        } else if (operation.verb in writingOperationVerbs) {
            commandCandidates.add(parentService)
        }

        operation.children().forEach { it.accept(visitor = this)}
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

    override fun visit(dependency: ServiceDependency) {
        if (dependency in visited) return

        visited.add(dependency)
        dependency.children().forEach { it.accept(visitor = this) }
    }
}