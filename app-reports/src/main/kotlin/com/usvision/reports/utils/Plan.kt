package com.usvision.reports.utils

import com.usvision.analyses.analyzer.Analyzer
import com.usvision.analyses.detector.Detector

class Plan : EditablePlan {
    private val steps: MutableList<Any> = mutableListOf()
    private var nextStepIndex: Int = 0

    override fun addStep(analyzer: Analyzer<*>) {
        steps.add(analyzer)
    }

    override fun addStep(detector: Detector) {
        steps.add(detector)
    }

    override fun hasNextStep(): Boolean = nextStepIndex < steps.size

    override fun getNextStep(): Any {
        val nextStep = steps[nextStepIndex]
        nextStepIndex++
        return nextStep
    }

    override fun size(): Int = steps.size
}