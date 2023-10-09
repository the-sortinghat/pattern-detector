package com.usvision.reports.utils

import com.usvision.analyses.analyzer.Analyzer
import com.usvision.analyses.detector.Detector

interface EditablePlan : ExecutablePlan {
    fun addStep(analyzer: Analyzer<*>)
    fun addStep(detector: Detector)
}