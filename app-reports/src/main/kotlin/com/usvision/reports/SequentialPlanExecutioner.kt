package com.usvision.reports

import com.usvision.model.System

class SequentialPlanExecutioner : PlanExecutioner {
    override fun execute(plan: Plan, system: System): Report {
        val (analyzers, detectors) = plan

        analyzers.forEach { system.accept(it) }
        val insightsGroupedByType = detectors
            .map {
                it.run()
                it.getInstances()
            }
            .flatten()
            .groupBy { it::class }

        return Report(insightsGroupedByType)
    }
}