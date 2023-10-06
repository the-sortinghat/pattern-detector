package com.usvision.reports

import com.usvision.analyses.Analyzer
import com.usvision.analyses.Detector

data class Plan(
    val analyzers: Set<Analyzer<Any>>,
    val detectors: Set<Detector>
)