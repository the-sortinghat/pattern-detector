package com.usvision.reports

interface Planner {
    fun plan(reportRequest: ReportRequest): Plan
}