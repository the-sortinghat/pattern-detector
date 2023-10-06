package com.usvision.reports

import com.usvision.model.System

interface PlanExecutioner {
    fun execute(plan: Plan, system: System): Report
}