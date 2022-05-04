package com.sortinghat.pattern_detector.domain.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DatabasePerServiceDetectorTest {

    private lateinit var underTest: DatabasePerServiceDetector

    @BeforeEach
    fun setup() {
        underTest = DatabasePerServiceDetector()
    }

    @Test
    fun `it detects dbps occurrence on the trivial case`() {
        // given
        val visitable = Scenarios.oneServiceOneDatabase()
        visitable.forEach { it.accept(MetricCollector()) }

        // when
        visitable.forEach { it.accept(underTest) }

        // then
        val results = underTest.getResults()
        assertEquals(1, results.size)
    }

    @Test
    fun `it correctly detects when service uses two databases`() {
        // given
        val visitable = Scenarios.oneServiceWithTwoDatabases()
        visitable.forEach { it.accept(MetricCollector()) }

        // when
        visitable.forEach { it.accept(underTest) }

        // then
        val results = underTest.getResults()
        assertEquals(1, results.size)
    }
}