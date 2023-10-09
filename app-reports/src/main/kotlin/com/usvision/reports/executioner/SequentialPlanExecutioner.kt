package com.usvision.reports.executioner

import com.usvision.analyses.analyzer.Analyzer
import com.usvision.analyses.detector.ArchitectureInsight
import com.usvision.analyses.detector.Detector
import com.usvision.model.systemcomposite.System
import com.usvision.reports.utils.ExecutablePlan
import com.usvision.reports.utils.Report

class SequentialPlanExecutioner : PlanExecutioner {
    private lateinit var system: System
    private lateinit var insights: MutableSet<ArchitectureInsight>

    override fun execute(plan: ExecutablePlan, system: System): Report {
        this.system = system
        this.insights = mutableSetOf()

        while (plan.hasNextStep()) {
            val step = plan.getNextStep()
            when (step) {
                is Analyzer<*> -> system.accept(step)
                is Detector -> runDetector(step)
            }
        }

        val insightsGroupedByType = insights.groupBy { it::class }

        return Report(insightsGroupedByType)
    }

    private fun runDetector(detector: Detector) {
        detector.run()
        detector
            .getInstances()
            .forEach { insights.add(it) }
    }
}