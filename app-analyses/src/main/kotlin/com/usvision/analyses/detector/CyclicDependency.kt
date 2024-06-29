package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.AsyncDependenciesOfMicroservice
import com.usvision.analyses.analyzer.Relationship
import com.usvision.analyses.analyzer.SyncDependenciesOfMicroservice
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class CyclicDependency(
    private val syncDependenciesOfMicroservice: SyncDependenciesOfMicroservice,
    private val asyncDependenciesOfMicroservice: AsyncDependenciesOfMicroservice
) : Detector() {
    private lateinit var instances: List<CyclicDependencyInstance>
    private lateinit var asyncDeps: Map<Visitable, Set<Relationship>>
    private lateinit var syncDeps: Map<Visitable, Set<Relationship>>
    private lateinit var deps: MutableMap<Microservice, MutableSet<Microservice>>

    override fun collectMetrics() {
        syncDeps = syncDependenciesOfMicroservice.getResults()
        asyncDeps = asyncDependenciesOfMicroservice.getResults()
    }

    override fun combineMetric() {
        deps = mutableMapOf()
        prepareSyncDeps()
        prepareAsyncDepsGivenSyncDeps()
        val allParents = findCycles()
        instances = allParents.keys.map { root ->
            val itsParents = allParents[root]!!
            CyclicDependencyInstance.fromParentRelations(root, itsParents)
        }
    }

    override fun getInstances(): Set<ArchitectureInsight> {
        return instances.toSet()
    }

    private fun prepareSyncDeps() {
        syncDeps.keys.forEach { ms ->
            val micro = ms as Microservice
            val rels = syncDeps[ms]!!
            deps[micro] = rels.map { it.with as Microservice }.toMutableSet()
        }
    }

    private fun prepareAsyncDepsGivenSyncDeps() {
        asyncDeps.keys.forEach { ms ->
            val micro = ms as Microservice
            val rels = asyncDeps[ms]!!
            val unwrappedRels = rels.map { it.with as Microservice }
            if (micro !in deps)
                deps[micro] = unwrappedRels.toMutableSet()
            else
                deps[micro]!!.addAll(unwrappedRels)
        }
    }

    private fun findOneCycle(
        startingFrom: Microservice,
        visited: MutableSet<Microservice>,
        parentOf: MutableMap<Microservice, Microservice>
    ): Boolean {
        if (startingFrom in visited) return true

        visited.add(startingFrom)
        deps[startingFrom]?.forEach { neighbor ->
            if (!parentOf.containsKey(neighbor)) {
                parentOf[neighbor] = startingFrom
                val isFound = findOneCycle(neighbor, visited, parentOf)
                if (isFound) return true
            }
        }

        return false
    }

    private fun findCycles(): Map<Microservice, Map<Microservice, Microservice>> {
        val cycles = mutableMapOf<Microservice, Map<Microservice, Microservice>>()
        deps.keys.forEach { microservice ->
            val parents = mutableMapOf<Microservice, Microservice>()
            val visited = mutableSetOf<Microservice>()
            val isFound = findOneCycle(startingFrom = microservice, visited = visited, parentOf = parents)
            if (isFound) {
                cycles[microservice] = parents
            }
        }

        return cycles
    }

}