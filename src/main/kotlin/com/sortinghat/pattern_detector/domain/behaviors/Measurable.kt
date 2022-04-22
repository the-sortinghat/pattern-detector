package com.sortinghat.pattern_detector.domain.behaviors

import com.sortinghat.pattern_detector.domain.model.Metrics

interface Measurable {
    fun get(metric: Metrics): Int

    fun increase(metric: Metrics)

    fun increase(metric: Metrics, amount: Int)

    fun decrease(metric: Metrics)

    fun decrease(metric: Metrics, amount: Int)
}