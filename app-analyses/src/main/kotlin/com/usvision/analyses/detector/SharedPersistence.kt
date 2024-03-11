package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.DatabaseUsage
import com.usvision.analyses.analyzer.Measure
import com.usvision.analyses.analyzer.NumberOfClients
import com.usvision.analyses.analyzer.Relationship
import com.usvision.model.domain.Microservice
import com.usvision.model.domain.databases.Database
import com.usvision.model.visitor.Visitable

class SharedPersistence(
    private val nClients: NumberOfClients,
    private val dbUsage: DatabaseUsage
) : Detector() {

    private lateinit var nClientsResults: Map<Visitable, Measure>
    private lateinit var usageResults: Map<Visitable, Set<Relationship>>
    private lateinit var rawInstances: List<Pair<Database, Set<Microservice>>>

    override fun collectMetrics() {
        nClientsResults = nClients.getResults()
        usageResults = dbUsage.getResults()
    }

    override fun combineMetric() {
        val candidates = multipleClientsDbs(nClientsResults)
        rawInstances = linkToClients(candidates)
    }

    private fun linkToClients(databases: Set<Database>): List<Pair<Database, Set<Microservice>>> {
        return usageResults
            .filter { (db, _) -> db in databases }
            .map { (db, relations) ->
                    val microservices = relations
                        .map { it.with }
                        .filterIsInstance<Microservice>()
                        .toSet()
                    Pair(db as Database, microservices)
            }
    }

    private fun multipleClientsDbs(nClientsResults: Map<Visitable, Measure>): Set<Database> {
        return nClientsResults
            .filter { (_, measure) -> (measure.value as Int) > 1 }
            .map { (db, _) -> db }
            .filterIsInstance<Database>()
            .toSet()
    }

    override fun getInstances(): Set<ArchitectureInsight> {
        return rawInstances.map(SharedPersistenceInstance::of).toSet()
    }
}