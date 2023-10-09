package com.usvision.reports.planner

import com.usvision.reports.utils.ExecutablePlan
import com.usvision.reports.utils.ReportRequest

interface Planner {
    fun plan(reportRequest: ReportRequest): ExecutablePlan
}