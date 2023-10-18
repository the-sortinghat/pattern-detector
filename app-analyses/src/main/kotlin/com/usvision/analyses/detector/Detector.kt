package com.usvision.analyses.detector

sealed class Detector {
    fun run() {
        collectMetrics()
        combineMetric()
    }

    abstract fun collectMetrics()
    abstract fun combineMetric()

    abstract fun getInstances(): Set<ArchitectureInsight>
}

