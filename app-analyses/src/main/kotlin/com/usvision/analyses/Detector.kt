package com.usvision.analyses

abstract class Detector {
    fun run() {
        collectMetrics()
        combineMetric()
    }

    abstract fun collectMetrics()
    abstract fun combineMetric()

    abstract fun getInstances(): Set<ArchitectureInsight>
}

interface ArchitectureInsight