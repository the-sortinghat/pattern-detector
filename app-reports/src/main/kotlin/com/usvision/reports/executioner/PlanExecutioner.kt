package com.usvision.reports.executioner

import com.usvision.model.systemcomposite.System
import com.usvision.reports.utils.ExecutablePlan
import com.usvision.reports.utils.Report

interface PlanExecutioner {
    fun execute(plan: ExecutablePlan, system: System): Report
}