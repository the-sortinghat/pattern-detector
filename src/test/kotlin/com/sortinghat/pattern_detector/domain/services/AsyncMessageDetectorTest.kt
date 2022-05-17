package com.sortinghat.pattern_detector.domain.services

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AsyncMessageDetectorTest {

    private lateinit var underTest: AsyncMessageDetector

    @BeforeEach
    fun setup() {
        underTest = AsyncMessageDetector()
    }

    @Test
    fun `it detects amsg correctly for the trivial case`() {
        // given
        val visitable = Scenarios.onePublisherOneSubscriber()
        val mCol = MetricCollector()
        visitable.forEach { it.accept(visitor = mCol) }

        // when
        visitable.forEach { it.accept(visitor = underTest) }

        // then
        val results = underTest.getResults()
        assertEquals(1, results.size)
    }

    @Test
    fun `it detects amsg correctly for a more complex case`() {
        // given
        val visitable = Scenarios.onePublisherTwoSubscribers()
        val mCol = MetricCollector()
        visitable.forEach { it.accept(mCol) }

        // when
        visitable.forEach { it.accept(visitor = underTest) }

        // then
        val results = underTest.getResults()
        assertEquals(2, results.size)
    }
}