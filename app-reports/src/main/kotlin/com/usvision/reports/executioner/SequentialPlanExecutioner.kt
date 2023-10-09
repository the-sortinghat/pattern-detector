package com.usvision.reports.executioner

import com.usvision.model.systemcomposite.System
import com.usvision.reports.utils.Plan
import com.usvision.reports.utils.Report

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