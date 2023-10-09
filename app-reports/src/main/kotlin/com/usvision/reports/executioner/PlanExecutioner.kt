package com.usvision.reports.executioner

import com.usvision.model.systemcomposite.System
import com.usvision.reports.utils.Plan
import com.usvision.reports.utils.Report

interface PlanExecutioner {
    fun execute(plan: Plan, system: System): Report
}