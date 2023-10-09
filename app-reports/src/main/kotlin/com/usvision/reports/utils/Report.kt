package com.usvision.reports.utils

import com.usvision.analyses.detector.ArchitectureInsight
import kotlin.reflect.KClass

class Report(
    private val value: Map<KClass<out ArchitectureInsight>, Any>
) : Map<KClass<out ArchitectureInsight>, Any> by value