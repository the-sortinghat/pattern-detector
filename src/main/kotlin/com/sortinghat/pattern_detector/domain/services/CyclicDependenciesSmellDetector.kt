package com.sortinghat.pattern_detector.domain.services

import com.sortinghat.pattern_detector.domain.behaviors.SmellDetector
import com.sortinghat.pattern_detector.domain.behaviors.Visitable
import com.sortinghat.pattern_detector.domain.behaviors.Visitor
import com.sortinghat.pattern_detector.domain.model.*
import com.sortinghat.pattern_detector.domain.model.smells.CyclicDependenciesSmell

class CyclicDependenciesSmellDetector(private val inputServices: List<Service>) : Visitor, SmellDetector {

    private val visited = mutableSetOf<Visitable>()
    private val cyclicDependencies = mutableSetOf<CyclicDependenciesSmell>()
    private val visitedServices = mutableSetOf<Service>()

    override fun visit(dependencies: ServiceDependency) {
        if (dependencies in visited) return

        visited.add(dependencies)
        dependencies.children().forEach { it.accept(visitor = this) }
    }

    override fun visit(service: Service) {
        if (service in visited || service in visitedServices) return

        val path = mutableListOf<Service>()
        detectCyclicDependencies(service, path)

        visited.add(service)
        visitedServices.add(service)
        service.children().forEach { it.accept(visitor = this) }
    }

    private fun detectCyclicDependencies(service: Service, path: MutableList<Service>) {
        if (service in path) {
            // Cyclic dependency detected
            val cyclicDependencyPath = path.subList(path.indexOf(service), path.size)
            val cyclicDependenciesSmell = CyclicDependenciesSmell.from(service, cyclicDependencyPath)
            cyclicDependencies.add(cyclicDependenciesSmell)
            return
        }

        path.add(service)

        service.dependencies.forEach { dependency ->
            val dependencyService = getServiceFromDependency(dependency)
            if (dependencyService != null && !visitedServices.contains(dependencyService)) {
                detectCyclicDependencies(dependencyService, path.toMutableList())
            }
        }

        path.remove(service)
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
        return cyclicDependencies.toSet()
    }
}