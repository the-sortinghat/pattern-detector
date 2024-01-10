package com.usvision.analyses.detector

import com.usvision.analyses.analyzer.Measure
import com.usvision.analyses.analyzer.NumberOfExposedOperations
import com.usvision.analyses.analyzer.NumberOfReadingExposedOperations
import com.usvision.analyses.analyzer.NumberOfWritingExposedOperations
import com.usvision.model.domain.Microservice
import com.usvision.model.visitor.Visitable

class Cqrs(
    private val nops: NumberOfExposedOperations,
    private val nReadOps: NumberOfReadingExposedOperations,
    private val nWriteOps: NumberOfWritingExposedOperations,
    private val asyncMessaging: AsyncMessaging
) : Detector() {
    private lateinit var raw: Map<Microservice, List<Microservice>>
    private lateinit var asyncDeps: Map<Microservice, List<Microservice>>
    private lateinit var nWritings: Map<Visitable, Measure>
    private lateinit var nReadings: Map<Visitable, Measure>
    private lateinit var nOperations: Map<Visitable, Measure>

    companion object {
        const val MAX_COHESIVE_THRESHOLD: Int = 9
        const val MIN_READING_ON_QUERY_THRESHOLD: Int = 1
        const val MIN_WRITING_ON_COMMAND_THRESHOLD: Int = 1
    }

    override fun collectMetrics() {
        nOperations = nops.getResults()
        nReadings = nReadOps.getResults()
        nWritings = nWriteOps.getResults()
        asyncDeps = asyncMessaging
            .getInstances()
            .groupBy(
                keySelector = { (it as AsyncMessagingInstance).subscriber },
                valueTransform = { (it as AsyncMessagingInstance).publisher }
            )
    }

    override fun combineMetric() {
        val queryCandidates = nOperations.keys.filter { ms ->
            val cohesive = nOperations[ms]!!.value as Int <= MAX_COHESIVE_THRESHOLD
            val readable = (nReadings[ms]?.value ?: 0) as Int >= MIN_READING_ON_QUERY_THRESHOLD

            cohesive && readable
        } as List<Microservice>

        val commandCandidates = nWritings.keys.filter { ms ->
            nWritings[ms]!!.value as Int >= MIN_WRITING_ON_COMMAND_THRESHOLD
        } as List<Microservice>

        raw = asyncDeps.filter { entry ->
            val sub = entry.key
            val pubs = entry.value

            val subInCandidates = sub in queryCandidates
            val pubsInCandidates = pubs.all { it in commandCandidates }

            subInCandidates && pubsInCandidates
        }
    }

    override fun getInstances(): Set<ArchitectureInsight> {
        return raw
            .map { entry -> CqrsInstance.of(
                query = entry.key,
                commands = entry.value
            ) }
            .toSet()
    }
}