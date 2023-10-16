package com.usvision.reports.utils

import com.usvision.analyses.detector.ArchitectureInsight
import kotlinx.serialization.Serializable

@Serializable
sealed interface Report : Map<String, List<ArchitectureInsight>>

@Serializable
class BaseReport(
    private val detectedStructures: Map<String, List<ArchitectureInsight>>
) : Map<String, List<ArchitectureInsight>> by detectedStructures, Report
