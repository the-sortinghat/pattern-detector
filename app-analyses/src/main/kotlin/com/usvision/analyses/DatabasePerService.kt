package com.usvision.analyses

import com.usvision.model.Database
import com.usvision.model.Microservice
import com.usvision.model.Visitable

class DatabasePerService(
    private val nops: NumberOfExposedOperations,
    private val nclients: NumberOfClients,
    private val dbUsage: DatabaseUsage
) : Detector() {
    companion object {
        const val MAX_COHESIVE_THRESHOLD: Int = 9
        const val MAX_CLIENTS_THRESHOLD: Int = 1
    }

    private lateinit var nopsResults: Map<Visitable, Measure>
    private lateinit var nclientsResults: Map<Visitable, Measure>
    private lateinit var usageResults: Map<Visitable, Set<Relationship>>
    private lateinit var instances: Set<DatabasePerServiceInstance>

    override fun collectMetrics() {
        nopsResults = nops.getResults()
        nclientsResults = nclients.getResults()
        usageResults = dbUsage.getResults()
    }

    override fun combineMetric() {
        val singleClientCandidateDBs = singleClientDBs(nclientsResults)
        val cohesiveCandidateMSs = cohesiveMSs(nopsResults)
        val pairs: Set<Pair<Microservice, Database>> = crossInformation(
            singleClientCandidateDBs,
            cohesiveCandidateMSs)
        instances = pairs.map { DatabasePerServiceInstance.of(it) }.toSet()
    }

    private fun crossInformation(
        dbCandidates: Set<Database>,
        msCandidates: Set<Microservice>
    ): Set<Pair<Microservice, Database>> {
        return usageResults
            .filter { (db, relations) ->
                val clients = relations.map { it.with }
                db in dbCandidates && clients.first() in msCandidates
            }
            .map { (db, singleMsRelations) -> Pair(singleMsRelations.first().with, db) }
            .filterIsInstance<Pair<Microservice,Database>>()
            .toSet()
    }

    private fun cohesiveMSs(nopsResults: Map<Visitable, Measure>): Set<Microservice> {
        return nopsResults
            .keys.filter { ms -> (nopsResults[ms]!!.value as Int) < MAX_COHESIVE_THRESHOLD }
            .filterIsInstance<Microservice>()
            .toSet()
    }

    private fun singleClientDBs(nclientsResults: Map<Visitable, Measure>): Set<Database> {
        return nclientsResults
            .keys.filter { db -> nclientsResults[db]!!.value == MAX_CLIENTS_THRESHOLD }
            .filterIsInstance<Database>()
            .toSet()
    }

    override fun getInstances(): Set<ArchitectureInsight> = instances

}

data class DatabasePerServiceInstance(
    val microservice: Microservice,
    val database: Database
) : ArchitectureInsight {
    companion object {
        fun of(pair: Pair<Microservice, Database>) = DatabasePerServiceInstance(
            microservice = pair.first,
            database = pair.second
        )
    }
}