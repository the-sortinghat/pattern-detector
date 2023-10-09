package com.usvision.reports.utils

import com.usvision.analyses.analyzer.Analyzer
import com.usvision.analyses.detector.Detector

data class Plan(
    val analyzers: Set<Analyzer<Any>>,
    val detectors: Set<Detector>
)