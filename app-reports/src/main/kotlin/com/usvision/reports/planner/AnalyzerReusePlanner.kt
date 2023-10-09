package com.usvision.reports.planner

import com.usvision.analyses.analyzer.Analyzer
import com.usvision.analyses.detector.Detector
import com.usvision.reports.utils.EditablePlan
import com.usvision.reports.utils.ExecutablePlan
import com.usvision.reports.utils.Plan
import com.usvision.reports.utils.ReportRequest
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

    private fun instantiateDetectors(reportRequest: ReportRequest) {
        reportRequest.detectors.forEach { detKClass ->
            val kTypeParams = getTypesOfConstructorParams(detKClass)
            ensureAnalyzerInstanceIsCached(kTypeParams)
            val paramValues = getAnalyzersFromCache(kTypeParams)
            val detInstance = instantiateDetector(detKClass, paramValues)
            detectors.add(detInstance)
        }
    }

    private fun craftPlan(): ExecutablePlan {
        val plan: EditablePlan = Plan()
        analyzers.values.forEach { plan.addStep(it) }
        detectors.forEach { plan.addStep(it) }
        return plan
    }

    override fun plan(reportRequest: ReportRequest): ExecutablePlan {
        instantiateDetectors(reportRequest)

        return craftPlan()
    }
}