package com.usvision.reports.utils

import com.usvision.analyses.Detector
import kotlin.reflect.KClass

data class ReportRequest(
    val detectors: Set<KClass<out Detector>>
)