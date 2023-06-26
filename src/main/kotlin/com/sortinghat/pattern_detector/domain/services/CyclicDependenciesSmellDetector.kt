package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.SmellDetector
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*
import com.sortinghat.pattern_detector.domain.model.smells.CyclicDependenciesSmell

class CyclicDependenciesSmellDetector(private val inputServices: List<Service>) : Visitor, SmellDetector {

    private val visited = mutableSetOf<Visitable>()
    private val cyclicDependencies = mutableMapOf<Service, MutableList<Service>>()
    private val cyclicDependenciesDetected = mutableSetOf<Service>()
    private val visitedServices = mutableSetOf<Service>()

    override fun visit(dependencies: ServiceDependency) {
        if (dependencies in visited) return

        visited.add(dependencies)
        dependencies.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(service: Service) {
        if (service in visited || service in visitedServices) return

        val path = mutableListOf<Service>()
        val cyclicDependencyPath = detectCyclicDependencies(service, path)

        if (cyclicDependencyPath.isNotEmpty() && service !in cyclicDependenciesDetected) {
            cyclicDependencies[service] = cyclicDependencyPath.toMutableList()
            cyclicDependenciesDetected.add(service)
        }

        visited.add(service)
        visitedServices.add(service)
        service.children().forEach { it.accept(visitor = this) }
    }

    private fun detectCyclicDependencies(service: Service, path: MutableList<Service>): List<Service> {
        if (service in path) {
            // Cyclic dependency detected
            return path.subList(path.indexOf(service), path.size)
        }

        path.add(service)

        var cyclicDependencyPath: List<Service> = emptyList()

        service.dependencies.forEach { dependency ->
            val dependencyService = getServiceFromDependency(dependency)
            if (dependencyService != null && !visitedServices.contains(dependencyService)) {
                cyclicDependencyPath = detectCyclicDependencies(dependencyService, path.toMutableList())
                if (cyclicDependencyPath.isNotEmpty()) {
                    return cyclicDependencyPath
                }
            }
        }

        return cyclicDependencyPath
    }

    private fun getServiceFromDependency(dependency: ServiceDependency): Service? {
        return inputServices.find { it.name == dependency.serviceDepId.name }
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

    override fun getResults(): Set<CyclicDependenciesSmell> {
        return cyclicDependencies.map { (service, dependencies) ->
            CyclicDependenciesSmell.from(service, dependencies)
        }.toSet()
    }
}