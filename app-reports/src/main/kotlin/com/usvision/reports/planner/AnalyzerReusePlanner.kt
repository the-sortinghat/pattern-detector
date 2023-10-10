package com.usvision.reports.planner

import com.usvision.analyses.analyzer.Analyzer
import com.usvision.analyses.detector.Detector
import com.usvision.reports.utils.EditablePlan
import com.usvision.reports.utils.ExecutablePlan
import com.usvision.reports.utils.Plan
import com.usvision.reports.utils.ReportRequest
import java.util.Stack
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.primaryConstructor

class AnalyzerReusePlanner : Planner {
    private lateinit var stack: Stack<KClass<*>>
    private lateinit var analyzersCache: MutableMap<Any, Analyzer<*>>
    private lateinit var detectorsCache: MutableMap<Any, Detector>

    private fun KType.toKClass(): KClass<out Any> {
        return Class
            .forName(toString())
            .kotlin
    }

    private fun initialize() {
        stack = Stack()
        analyzersCache = mutableMapOf()
        detectorsCache = mutableMapOf()
    }

    private fun prepareStack(starterKClass: KClass<out Any>) {
        println("adding to stack $starterKClass")
        stack.push(starterKClass)

        starterKClass
            .primaryConstructor!!
            .parameters
            .map { it.type.toKClass() }
            .forEach { prepareStack(it) }
    }

    private fun getFromCache(kClass: KClass<out Any>): Any? {
        return analyzersCache[kClass] ?: detectorsCache[kClass]
    }

    private fun addToCache(instance: Any) {
        if (instance is Detector) detectorsCache[instance::class] = instance
        else analyzersCache[instance::class] = instance as Analyzer<*>
    }

    private fun instantiate(kClass: KClass<*>): Any {
        val paramsKClasses = kClass
            .primaryConstructor!!
            .parameters
            .map { it.type.toKClass() }

        val params = paramsKClasses
            .map(this::getFromCache)
            .toTypedArray()

        return if (params.isEmpty()) kClass.primaryConstructor!!.call()
        else kClass.primaryConstructor!!.call(*params)
    }

    private fun createPlan(): ExecutablePlan {
        val plan: EditablePlan = Plan()

        while (stack.isNotEmpty()) {
            val kClass = stack.pop()

            val instance = getFromCache(kClass)
                ?: instantiate(kClass).also { addToCache(it) }

            if (!plan.contains(instance)) plan.addStep(instance)
        }

        return plan
    }

    override fun plan(reportRequest: ReportRequest): ExecutablePlan {
        initialize()

        reportRequest.detectors.forEach { prepareStack(it) }

        return createPlan()
    }
}