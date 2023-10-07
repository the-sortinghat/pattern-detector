package com.usvision.reports

import com.usvision.analyses.Analyzer
import com.usvision.analyses.Detector
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

class AnalyzerReusePlanner : Planner {
    private val analyzers: MutableMap<KType, Analyzer<*>> = mutableMapOf()
    private val detectors: MutableSet<Detector> = mutableSetOf()

    private fun getTypesOfConstructorParams(detKClass: KClass<out Detector>): List<KType> {
        return detKClass
            .primaryConstructor!!
            .parameters
            .map { it.type }
    }

    private fun ensureAnalyzerInstanceIsCached(kTypeParams: List<KType>) {
        kTypeParams
            .filter { it !in analyzers.keys }
            .forEach { kType ->
                val analyzer = Class
                    .forName(kType.toString())
                    .getDeclaredConstructor()
                    .newInstance() as Analyzer<Any>
                analyzers[kType] = analyzer
            }
    }

    private fun getAnalyzersFromCache(kTypeParams: List<KType>): Array<Analyzer<*>?> {
        return kTypeParams
            .map { analyzers[it] }
            .toTypedArray()
    }

    private fun instantiateDetector(detKClass: KClass<out Detector>, paramValues: Array<Analyzer<*>?>): Detector {
        return detKClass
            .primaryConstructor!!
            .call(*paramValues)
    }

    override fun plan(reportRequest: ReportRequest): Plan {
        reportRequest.detectors.forEach { detKClass ->
            val kTypeParams = getTypesOfConstructorParams(detKClass)
            ensureAnalyzerInstanceIsCached(kTypeParams)
            val paramValues = getAnalyzersFromCache(kTypeParams)
            val detInstance = instantiateDetector(detKClass, paramValues)
            detectors.add(detInstance)
        }

        return Plan(
            analyzers = analyzers.values.toSet() as Set<Analyzer<Any>>,
            detectors = detectors
        )
    }
}