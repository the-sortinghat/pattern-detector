package com.sortinghat.pattern_detector.domain.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class SingleServicePerHostDetectorTest {

    private lateinit var underTest: SingleServicePerHostDetector

    @BeforeEach
    fun setup() {
        underTest = SingleServicePerHostDetector()
    }

    @Test
    fun `it detects single svc on the trivial case`() {
        // given
        val visitable = Scenarios.oneModuleWithOneService()
        visitable.forEach { it.accept(visitor = MetricCollector()) }

        // when
        visitable.forEach { it.accept(underTest) }
        val results = underTest.getResults()

        // then
        assertEquals(1, results.size)
    }
}