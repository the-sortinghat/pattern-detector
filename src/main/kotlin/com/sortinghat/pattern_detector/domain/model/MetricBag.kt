package com.sortinghat.pattern_detector.domain.model

import com.sortinghat.pattern_detector.domain.behaviors.Measurable

class MetricBag : Measurable {
    private val measures: MutableMap<Metrics, Int> = mutableMapOf()

    init {
        Metrics.values().forEach { measures[it] = 0 }
    }

    override fun get(metric: Metrics): Int = measures[metric] ?: 0

    override fun increase(metric: Metrics) {
        increase(metric, 1)
    }

    override fun increase(metric: Metrics, amount: Int) {
        measures[metric] = if (measures[metric] != null) measures[metric]!! + amount else amount
    }

    override fun decrease(metric: Metrics) {
        decrease(metric, 1)
    }

    override fun decrease(metric: Metrics, amount: Int) {
        measures[metric] = if (measures[metric] != null) measures[metric]!! - amount else 0

        if (measures[metric]!! < 0) measures[metric] = 0
    }
}
