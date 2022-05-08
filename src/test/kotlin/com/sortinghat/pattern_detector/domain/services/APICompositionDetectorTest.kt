package com.sortinghat.pattern_detector.domain.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class APICompositionDetectorTest {

    private lateinit var underTest: APICompositionDetector

    @BeforeEach
    fun setup() {
        underTest = APICompositionDetector()
    }

    @Test
    fun `it correctly detects apicomp for the trivial case`() {
        // given
        val visitable = Scenarios.oneQueryServiceWithTwoSyncDependencies()
        visitable.forEach { it.accept(visitor = MetricCollector()) }

        // when
        visitable.forEach { it.accept(visitor = underTest) }

        // then
        val results = underTest.getResults()
        assertEquals(1, results.size)
    }
}