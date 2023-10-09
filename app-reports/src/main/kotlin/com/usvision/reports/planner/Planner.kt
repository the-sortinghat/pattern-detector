package com.usvision.reports.planner

import com.usvision.reports.utils.Plan
import com.usvision.reports.utils.ReportRequest

interface Planner {
    fun plan(reportRequest: ReportRequest): Plan
}