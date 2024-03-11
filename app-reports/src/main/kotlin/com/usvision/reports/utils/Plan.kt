package com.usvision.reports.utils

class Plan : EditablePlan {
    private val steps: MutableList<Any> = mutableListOf()
    private var nextStepIndex: Int = 0

    override fun contains(step: Any): Boolean {
        return steps.contains(step)
    }

    override fun addStep(step: Any) {
        steps.add(step)
    }

    override fun hasNextStep(): Boolean = nextStepIndex < steps.size

    override fun getNextStep(): Any {
        val nextStep = steps[nextStepIndex]
        nextStepIndex++
        return nextStep
    }

    override fun size(): Int = steps.size
}